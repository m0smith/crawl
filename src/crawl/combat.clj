(ns crawl.combat
  (:require [crawl.dice :refer :all]))


(defn attack [{:keys [attack-dice damage-dice ] :as attacker} 
              {:keys [ac hp] :as defender}]
  (let [a-type (:type attacker)
        d-type (:type defender)]
    (if (>= (throw-dice attack-dice) ac)
      (let [damage (throw-dice damage-dice)
            new-hp (- hp damage)
            diff { :hp new-hp }]
        [:hit attacker (merge defender diff) diff (str a-type " hits " d-type " for " damage) ])
      [:miss attacker defender {} (str a-type " misses " d-type) ])))

      
