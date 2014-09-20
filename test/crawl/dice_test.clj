(ns crawl.dice-test  
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [crawl.core :refer :all]
            [crawl.dice :refer :all]))


  

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

(deftest roll-die-noarg []
  (testing "The default die roll is between 1 and 20"
    (tc/quick-check 100 (arg-prop 20 20 1 roll-default-die-gen roll-die?))))

(deftest roll-die-arg []
  (testing "Check that a die roll is between 1 and the number of sides"
    (tc/quick-check 100 (arg-prop 2 100 1 roll-die-gen roll-die?))))

(deftest roll-dice-noarg []
  (testing "The default dice rolls are between 1 and 20"
    (tc/quick-check 100 (arg-prop 20 20 100 roll-default-dice-gen roll-dice?))))

(deftest roll-dice-arg []
  (testing "Check that dice rolls are between 1 and the number of sides"
    (tc/quick-check 100 (arg-prop 2 100 100 roll-dice-gen roll-dice?))))


;;  
