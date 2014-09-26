(ns crawl.dice-test  
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :as ct]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [crawl.core :refer :all]
            [crawl.dice :refer :all]))


  

(def gen-dice-def
  (gen/tuple gen/nat gen/nat gen/int))



(def noarg-prop 
  (prop/for-all [v (gen/fmap (fn [x] (roll-die)) gen/int)]
                (let [r v]
                  (and (> r 0) (< r 21)))))

(defn roll-default-die-gen [sides dice] (roll-die))

(defn roll-die-gen [sides dice] (roll-die sides))

(defn roll-default-dice-gen [sides dice] (roll-dice dice))

(defn roll-dice-gen [sides dice] (roll-dice dice sides))

(defn roll-die? [sides dice value]
  (and (> value 0) (<= value sides)))

(defn roll-dice? [sides dice value]
  (and (= dice (count value))
       (every? identity 
               (for [v value]
                 (roll-die? sides dice v)))))

(defn sides-and-roll [sides-lower sides-upper dice-upper roll-fn]
  (gen/fmap (fn [[sides dice]] [sides dice (roll-fn sides dice)])
            (gen/tuple (gen/choose sides-lower sides-upper) (gen/choose 1 dice-upper))))




(defn arg-prop [sides-lower sides-upper dice-upper roll-fn pred]
  (prop/for-all [sdv (gen/not-empty  (gen/vector (sides-and-roll sides-lower sides-upper dice-upper roll-fn)))]
                (every? identity 
                        (for [[sides dice value] sdv]
                          (pred sides dice value)))))

;; ## dice/throw-dice
;;

(defn throw-dice-gen
  "Generator for inputs to throw-dice"
  []
  (gen/tuple gen/s-pos-int gen/s-pos-int gen/int))


(defn throw-dice?
  "Validate the `throw-dice` function for the arguments. 

  The value must be between `modifier + dice` and `modifier + (dice * sides)`

"
  [dice sides modifier]
  (let [r (throw-dice dice sides modifier)
        min (+ modifier dice)
        max (+ modifier (* sides dice))]
    (<= min r max)))


;; ## Start the tests

(ct/defspec roll-die-noarg 100 
  (arg-prop 20 20 1 roll-default-die-gen roll-die?))

(ct/defspec roll-die-arg  100 
  (arg-prop 2 100 1 roll-die-gen roll-die?))

(ct/defspec roll-dice-noarg  100 
  (arg-prop 20 20 100 roll-default-dice-gen roll-dice?))

(ct/defspec roll-dice-arg  100 
  (arg-prop 2 100 100 roll-dice-gen roll-dice?))


(ct/defspec throw-dice-3arg 100
  (prop/for-all [s (throw-dice-gen)] 
                (apply throw-dice? s)))


(ct/defspec test-roll-damage 100
  (prop/for-all [dice-def gen-dice-def]
                (let [roll (roll-damage dice-def)]
                  (>= roll 0))))

