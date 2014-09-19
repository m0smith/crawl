(ns crawl.dice-test  
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [crawl.core :refer :all]
            [crawl.dice :refer :all]))


  

(def noarg-prop 
  (prop/for-all [v (gen/fmap (fn [x] (roll)) gen/int)]
                (let [r v]
                  (and (> r 0) (< r 21)))))

(defn sides-and-roll [lower upper]
  (let [rtnval (gen/bind (gen/choose lower upper)
                         (fn [v] (gen/tuple (gen/return v) (gen/return (roll v)))))]
    rtnval))

(defn arg-prop [lower upper]
  (prop/for-all [[v r] (sides-and-roll lower upper)]
                (and (> r 0) (<= r v))))

(deftest roll-noarg []
  (testing "The default dice roll is between 1 and 20"
    (tc/quick-check 100 noarg-prop)))

(deftest roll-arg []
  (testing "Check that a dice roll is between 1 and the number"
    (tc/quick-check 100 (arg-prop 2 100))))


;;  
