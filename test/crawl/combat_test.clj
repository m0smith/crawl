(ns crawl.combat-test  
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [crawl.combat :refer :all]
            [crawl.monster :refer :all]
            ))




;; ## Attack
;; ### Generators:
;; * two Monsters: [id pid type ac max-hp hp attack-dice damage-dice loot]
;; ### Properties:
;; * 
  




(def gen-dice-throw
  (gen/tuple gen/nat gen/nat gen/int))

(def gen-keyword (gen/fmap keyword (gen/not-empty gen/string-ascii)))

(def gen-id gen-keyword)
(def gen-pid gen-keyword)
(def gen-type (gen/not-empty gen/string-ascii) )
(def gen-ac gen/s-pos-int)
(def gen-max-hp gen/s-pos-int)
(def gen-hp gen/nat)
(def gen-attack-dice gen-dice-throw)
(def gen-damage-dice gen-dice-throw)
(def gen-loot gen/s-pos-int)


(def gen-monster
 (gen/fmap (partial apply ->Monster)
           (gen/tuple gen-id gen-pid gen-type gen-ac gen-max-hp gen-hp gen-attack-dice gen-damage-dice gen-loot)))

(defn monsters [at de] )


;; ## Start the tests

(deftest combat-test []
  (testing "Check `combat` returns a valid value"
    (tc/quick-check 100 (prop/for-all [at (gen-monster) de (gen-monster)] (apply monsters at de)))))



