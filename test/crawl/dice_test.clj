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

(defn sides-and-roll [lower upper]
  (let [rtnval (gen/bind (gen/choose lower upper)
                         (fn [v] (gen/tuple (gen/return v) (gen/return (roll-die v)))))]
    rtnval))

(defn arg-prop [lower upper]
  (prop/for-all [vr (gen/not-empty  (gen/vector (sides-and-roll lower upper)))]
                (every? identity 
                        (for [[v r] vr]
                          (and (> r 0) (<= r v))))))

(deftest roll-die-noarg []
  (testing "The default dice roll is between 1 and 20"
    (tc/quick-check 100 noarg-prop)))

(deftest roll-die-arg []
  (testing "Check that a dice roll is between 1 and the number"
    (tc/quick-check 100 (arg-prop 2 100))))


;;  
