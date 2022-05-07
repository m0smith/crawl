(ns crawl.d20.attack
  (:require
   [crawl.d20.dice :refer [define-dice roll]]))

(defprotocol PAttacker
  (create-attack [attacker] "Create a PAttack instance"))

(defprotocol PAttack
  (hit-roll [a]))

(defprotocol PDefender
  (defend-attack [defender attack]))

(defrecord Attack [attacker hit-dice roll]
  PAttack
  (hit-roll [_] (:value roll)))

(defn attack [attacker defender]
  (let [attack (create-attack attacker)]
    {:attack attack
     :defence (defend-attack defender attack)}))

(defn make-creature [hit-dice hp armor-class]
  (reify
    
    PAttacker
    (create-attack [attacker]
      (->Attack attacker hit-dice (roll hit-dice)))
    
    PDefender
    (defend-attack [defender attack]
      {:hp hp
       :armor-class armor-class
       :successful? (<= armor-class (hit-roll attack))
       :defender defender})))

(def catt (make-creature (define-dice 20 1 [{:value 1}]) 10 15))
(def rat (make-creature (define-dice 20 1 [{:value 0}]) 8 14))

