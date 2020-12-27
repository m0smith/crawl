(ns crawl.dice)

;; dice def:  is a triple of `[dice sides modifier]`.  
;; throw spec is a string like `"1d5+3"` or `"1d5-3"` or `"2d10"`

(def default-sides 20)

(defrecord DiceSpec [dice sides modifier]
  Object
  (toString [ds] (str dice "d" sides (if (neg? modifier) "" "+") modifier)))

(defn roll-die [{:keys [dice sides modifier] :as dice-spec}]
  (let [roll (inc (rand-int sides))
        type (keyword (str "d" sides))]
    (cond-> {:type type :value roll}
      (= 1 roll) (assoc :min true)
      (= sides roll) (assoc :max true))))

(defn parse
  "Accept strings in the format 1d12+8 and return a seq of dice def:
   
     `[dice side modifier]` 

  Modifier can be nil, a positive integer or a
   negative integer.

  If the string is not parsable, rturn an empty seq.
"
  [s]
  (for [thrw (re-seq #"(\d+)d(\d+)([\+-]\d+)?" s)]
    (let [[_ dice sides modifier] thrw
          dice (if dice (Integer/parseInt dice) 0)
          sides (if sides (Integer/parseInt sides) 0)
          modifier (if modifier (Integer/parseInt modifier) 0)]
      (->DiceSpec dice sides modifier))))

(defprotocol NormalizeSpec
  (normalize [d] "Return a seq of DiceSpec"))

(defn rr [acc v]
  (if (instance? DiceSpec v)
    (conj acc v)
    (concat acc v)))

(defn normalize-vector
  "A vector can be a seq of specs or a single [dice sides modifier]."
  [[dice sides modifier :as v]]
  (if (number? dice)
    [(->DiceSpec dice sides modifier)]
    (reduce rr [] (map normalize v))))

(defn normalize-seq [v]
  (reduce rr [] (map normalize v)))

(extend-protocol NormalizeSpec
  DiceSpec
  (normalize [ds] [ds])

  String
  (normalize [s] (parse s))

  clojure.lang.IPersistentVector
  (normalize [v] (normalize-vector v))
  
  clojure.lang.Seqable
  (normalize [v] (normalize-seq v)))

(defn roll
  ([k specs]
   {k (roll specs)})
  ([k specs k2 specs2]
   {k (roll specs)
    k2 (roll specs2)})
  ([k specs k2 specs2 k3 specs3]
   {k (roll specs)
    k2 (roll specs2)
    k3 (roll specs3)})
  ([specs]
   (let [dice-specs (normalize specs)
         modifier (apply + (map :modifier dice-specs))
         rolls (map roll-die dice-specs)
         total (apply + modifier (map :value rolls))
         by-type (group-by :type rolls)]
     {:total total
      :modifier modifier
      :dice rolls
      :by-type by-type})))

(defn roll-with-advantage [attack-specs damage-specs]
  (let [{:keys[first second] :as result} (roll :first attack-specs
                                               :second attack-specs
                                               :damage damage-specs)
        attack (if (> (:total first) (:total second)) first second)]
    (assoc result :attack attack)))


(defn roll-with-disadvantage [attack-specs damage-specs]
  (let [{:keys[first second] :as result} (roll :first attack-specs
                                               :second attack-specs
                                               :damage damage-specs)
        attack (if (< (:total first) (:total second)) first second)]
    (assoc result :attack attack)))

(defn roll-die-old 
  "Roll a single die and get a number between 1 and the number of
  sides passed.  "
  ([] (roll-die-old default-sides))
  ([sides]
   (let [roll (inc (rand-int sides))
         type (keyword (str "d" sides))]
     {:type type :value roll})))
  
(defn roll-dice
  "Roll the number of dice and return the results as a seq.  If sides
  is not passed, default-sides is used."
  ([dice] (roll-dice dice default-sides))
  ([dice sides] (take dice (repeatedly #(roll-die-old sides)))))



(defn throw-dice
  "A function to throw dice and return the result.   The function
  is called with a throw spec or three integers: dice, sides and modifier.

  The result is {:total <int> :dice [{:type :d4 :value 3}+]

  The specified number of dice having the given number of side are
  rolled and their total is added to modifier.  Modifier can be
  negative.

  "

  ([[dice sides modifier]]
     (throw-dice dice sides modifier))

  ([dice sides modifier]
   (let [modifier (or modifier 0)
         rolls (roll-dice dice sides)
         total (apply +  modifier (map :value rolls))]
     {:total total :dice rolls :modifier modifier})))

(defn throw-attack-dice 
  ""
  [m]
  (let [r (roll-die-old)]
    (if (= r default-sides)
      :critical-hit
      (+ r m))))

;; ## throw spec handling


		
(defn parse-throw-spec
  "Accept strings in the format 1d12+8 and return a seq of dice def:

   
     `[dice side modifier]` 

  Modifier can be nil, a positive integer or a
   negative integer.

  If the string is not parsable, rturn an empty seq.

  
"
  [s]
  (for [thrw (re-seq #"(\d+)d(\d+)([\+-]\d+)?" s)]
    (->> thrw
         (drop 1)
         (map #(if % (Integer/parseInt %) 0))
         vec)))
              
(defn merger [v1 v2]
  (if (number? v1)
    (+ v1 v2)
    (concat v1 v2)))

(defn parse-and-throw-dice
  "A function to throw dice and return the result.  The dice can
  either be a string like '1d4' or '1d4+9' or '1d4-9'.  Also the
  string can have multiple dice defs like '1d8+9 2d4+1'.  The dice
  defs have to be separated by a space or some other non-numerica
  character

  The specified number of dice having the given number of side are
  rolled and their total is added to modifier.  Modifier can be
  negative."

  ([s]
   (let [result (apply merge-with merger (map throw-dice (parse-throw-spec s)))
         by-type (group-by :type (:dice result))]
     (assoc result :by-type by-type))))

;; ## Other random related functions

(defn roll-choose
  "Take a vector and return a random element"
  ([v] (rand-nth v)))

  
(defn roll-chance [top total]
  (let [r (roll-die-old total)]
    (if ( <= r top)
      r
      nil)))

(defn roll-for-monster 
  "roll to see if a new monster is created using a 1 in 5 chance.
  
  On nil, do not create a monster.  On non-nil, create a monster"
  []
  (roll-chance 1 5))


(defn roll-initiative 
  "to roll initiative, roll 2 d20, one for each combatant.  return 0 for first combatant,
  1 for the second.  In case of tie, return 0 for even, 1 for odd"
  []
  (let [r1 (roll-die-old)
        r2 (roll-die-old)]
    (cond
     (> r1 r2) 0
     (< r1 r2) 1
     (= r1 r2) (mod r1 2))))

(defn roll-damage 
  "Damage cannot be less than 0"
  [damage-dice]
  (let [rtnval (throw-dice damage-dice)]
    (if (< rtnval 0)
      0
      rtnval)))
