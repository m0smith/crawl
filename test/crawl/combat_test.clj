(ns crawl.combat-test  
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :as ct]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [crawl.combat :refer :all]
            [crawl.monster :refer :all]
            [crawl.dice-test  :refer :all]
            ))




;; ## Attack
;; ### Generators:
;; * MonsterPrototype [pid type ac max-hp attack-dice damage-dice loot]
;; * two Monsters: [id pid type ac max-hp hp attack-dice damage-dice loot]
;; ### Properties:
;; * 
  


(def gen-keyword (gen/fmap keyword (gen/not-empty gen/string-ascii)))

(def gen-id gen-keyword)
(def gen-pid gen-keyword)
(def gen-type (gen/not-empty gen/string-ascii) )
(def gen-ac gen/s-pos-int)
(def gen-ac-dice gen-dice-def)
(def gen-max-hp-dice gen-dice-def)
(def gen-max-hp gen/s-pos-int)
(def gen-hp gen/nat)
(def gen-attack-dice gen-dice-def)
(def gen-damage-dice gen-dice-def)
(def gen-loot gen/s-pos-int)
(def gen-loot-dice gen-dice-def)

(def gen-monster-prototype
 (gen/fmap (partial apply ->MonsterPrototype)
           (gen/tuple gen-pid gen-type gen-ac-dice gen-max-hp-dice gen-attack-dice gen-damage-dice gen-loot-dice)))

(def gen-monster
 (gen/fmap create-monster gen-monster-prototype))

(def gen-attack
  (gen/fmap (fn [[at de ]] [at de (attack at de)]) (gen/tuple gen-monster gen-monster)))


;; ## Validation functions

(defn validate-stat [_ _ [stat & rest]]
  (#{:hit :miss } stat))

(defn validate-attacker [at _ [_ new-at & rest]]
  (= at new-at))

(defn validate-defender [_ {:keys [hp] :as de} [stat _ new-de & rest]]
  (let [new-hp (:hp new-de)]
    (and (<= new-hp hp)
         (if (= stat :miss)
           (= de new-de)
           true))))


;; ## Start the tests

(ct/defspec combat-stat-test 100
  (prop/for-all [at gen-attack] 
                ( apply validate-stat at)))

(ct/defspec combat-attacker-test 100
  (prop/for-all [at gen-attack] 
                ( apply validate-attacker at)))

(ct/defspec combat-defender-test 100
  (prop/for-all [at gen-attack] 
                ( apply validate-defender at)))





