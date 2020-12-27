(ns crawl.combat
  (:require
   [crawl.creature :refer [attack-dice damage-dice]]
   [crawl.dice :refer [roll]]
   [crawl.event :refer [Event]]
   [crawl.object :refer [armor-class name-of]]
   ))


(defn result-description [result]
  (if (= :miss result)
    "misses"
    "hits"))

(defn damage-description [damage]
  (when damage
    (condp = damage
      1 " for 1 hit point of damage"
      (str " for " damage " hit points of damage"))))

(defrecord AttackOutcome [result armor-class attack-value damage damage-roll attack-roll attacker target]
  Event
  (short-description [s] (str "The " (name-of attacker) " " (result-description result) " the " (name-of target)
                              (damage-description damage) ".")))

(defn attack [attacker target]
  (let [ad (attack-dice attacker)
        dd (damage-dice attacker)
        ac (armor-class target)
        {:keys [rolled damage]} (roll :rolled ad :damage dd)
        attack-val (:total rolled)]
    (if (<= ac attack-val)
      (->AttackOutcome :hit ac attack-val (:total damage) damage rolled attacker target)
      (->AttackOutcome :miss ac attack-val nil nil rolled attacker target))))







      
