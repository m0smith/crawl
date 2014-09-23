(ns crawl.dice)

;; dice def:  is a triple of `[dice sides modifier]`.  
;; throw spec is a string like `"1d5+3"` or `"1d5-3"` or `"2d10"`

(def default-sides 20)

(defn roll-die 
  "Roll a single die and get a number between 1 and the number of
  sides passed.  "
  ([] (roll-die default-sides))
  ([sides] (inc (rand-int sides))))
  
(defn roll-dice
  "Roll the number of dice and return the results as a seq.  If sides
  is not passed, default-sides is used."
  ([dice] (roll-dice dice default-sides))
  ([dice sides] (take dice (repeatedly #(roll-die sides)))))



(defn throw-dice
  "A function to throw dice and return the result.   The function
  is called with a throw spec or three integers: dice, sides and modifier.

  The specified number of dice having the given number of side are
  rolled and their total is added to modifier.  Modifier can be
  negative."

  ([[dice sides modifier]]
     (apply + modifier (roll-dice dice sides)))

  ([dice sides modifier]
     (apply + modifier (roll-dice dice sides))))

(defn throw-attack-dice 
  ""
  [m]
  (let [r (roll-die)]
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
              


(defn parse-and-throw-dice
  "A function to throw dice and return the result.  The dice can
  either be a string like '1d4' or '1d4+9' or '1d4-9'.  Also the
  string can have multiple dice defs like '1d8+9 2d4+1'.  The dice
  defs have to be separated by a space or some other non-numerica
  character

  The specified number of dice having the given number of side are
  rolled and their total is added to modifier.  Modifier can be
  negative."

  ([s] (apply + (for [thrw (parse-throw-spec s)]
                  (apply throw-dice thrw)))))

;; ## Other random related functions

(defn roll-choose
  "Take a vector and return a random element"
  ([v] (rand-nth v)))

  
(defn roll-chance [top total]
  (let [r (roll-die total)]
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
  (let [r1 (roll-die)
        r2 (roll-die)]
    [ r1 r2
    (cond
     (> r1 r2) 0
     (< r1 r2) 1
     (= r1 r2) (mod r1 2))]))
