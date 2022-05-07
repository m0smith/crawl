(ns crawl.d20.dice
  (:require
   [clojure.spec.alpha :as s]
   
   [crawl.random :refer :all]))

(defn update-keys [combine-fn ky]
  (fn [m1 m2]
    (update m1 ky combine-fn (m2 ky))))

(defn apply-merge-fns
  "Accepts a list of functions that work with reduce.
  Returns a function that accepts 2 variables.
  Runs reduce by appyling each function `acc` and `i`"
  [& reduce-fns]
  (fn [acc i]
    (reduce (fn apply-merge-fns-reduce [a f] (f a i)) acc reduce-fns)))

(defn n-sided-dice-roller
  ([sides] (n-sided-dice-roller 1 sides))
  ([least greatest]
   (fn []
     {:value (random-in-range least greatest)
      :min-roll least
      :max-roll greatest})))

(def add-rolls
  "A `roll-all` merge-fn for adding 2 rolls together."
  (apply-merge-fns
   (update-keys + :value)
   (update-keys + :min-roll)
   (update-keys + :max-roll)
   (update-keys into :rolls)
   (update-keys into :modifiers)))

(defn keep-greatest
  "Combine 2 rolls, taking the larger of the 2.  The :rolls are kept in order.
  For use the `roll-all`"
  [{v1 :value :as m1}
   {v2 :value :as m2}]
  (let [rolls (into (:rolls m1) (:rolls m2))]
    (assoc (if (> v1 v2) m1 m2) :rolls rolls)))

(defn keep-least
  "Combine 2 rolls, taking the smaller of the 2.  The :rolls are kept in order
  For use with `roll-all`"
  [{v1 :value :as m1}
   {v2 :value :as m2}]
  (let [rolls (into (:rolls m1) (:rolls m2))]
    (assoc (if (< v1 v2) m1 m2) :rolls rolls)))

(defprotocol PRoll
  (roll [d] "Roll the bones"))

(defn roll-all
  "Roll several PRoll (DiceDefinition) and combine the results into a single map.
  To keep the rolls separate use:
     (mapv roll [d d d d])"
  [merge-fn & dice-definitions]
  (reduce merge-fn (map roll dice-definitions)))

(defn roll-dice
  [d]
  (let [{:keys [modifiers sides quantity combine-fn roller]} d
        rolls (into [] (repeatedly quantity roller))
        modifier (apply + (map :value modifiers))]
    {:value (+ modifier (reduce combine-fn 0 (map :value rolls)))
     :modifier modifier
     :min-roll (+ modifier (reduce combine-fn 0 (map :min-roll rolls)))
     :max-roll (+ modifier (reduce combine-fn 0 (map :max-roll rolls)))
     :rolls rolls
     :modifiers modifiers}))

(defrecord DiceDefinition [sides quantity modifiers combine-fn roller]
  PRoll
  (roll [d] (roll-dice d)))

(defn define-dice
  ([sides] (define-dice sides 1 []))
  ([sides quantity] (define-dice sides quantity []))
  ([sides quantity modifiers] (define-dice sides quantity modifiers + (n-sided-dice-roller sides)))
  ([sides quantity modifiers combine-fn roller]
   (->DiceDefinition sides quantity modifiers combine-fn roller)))

(s/fdef *10+
  :args (s/and (s/cat :start int? :end int?)
               #(< (:start %) (:end %)))
  :ret number?
  :fn #(= (:ret %) (+' (*' 10 (-> % :args :start))(-> % :args :end))))

(defn *10+
  "Multiple `acc` by 10 and add `i`"
  [tens ones]
  (+' ones (*' 10 tens)))

(defn define-percentile-dice
  []
  (let [sides 10
        least 0
        greatest (dec sides)
        quantity 2
        modifiers []
        dd (define-dice greatest quantity modifiers *10+ (n-sided-dice-roller least greatest))]
    (reify PRoll
      (roll [_]
        (let [{:keys [max-roll value] :as rtnval} (->(roll dd)
                                                     (update :min-roll inc)
                                                     (update :max-roll inc))]
          (cond-> rtnval
            (= value 0) (assoc :value max-roll)))))))

(defn roll-with-advantage [dd]
  (roll-all keep-greatest dd dd))

(defn roll-with-disadvantage [dd]
  (roll-all keep-least dd dd))

(def d20 (define-dice 20))
(def d12 (define-dice 12))
(def d10 (define-dice 10))
(def d6 (define-dice 6))
(def d4 (define-dice 4))
(def percentile (define-percentile-dice))


