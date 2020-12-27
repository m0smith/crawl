(ns crawl.core
  (:require [crawl.creature :refer [->Creature ->CreatureType ->zoo]]
            [crawl.combat :refer [attack]]
            [crawl.object :refer [hit-points]]
            [crawl.state :refer [->GameState]]
            [crawl.monster :as monster]))

(def game-state (->GameState
                 (->zoo [(->Creature "small rat" monster/Rat 10)
                         (->Creature "pale snake" monster/Snake 10)
                         ])
                 :running))

(defn roll-initative [zoo]
  (shuffle (keys zoo)))

(defn begin-combat-> [game]
  (let [initative (roll-initative (:zoo game))]
    (assoc game
           :combat {:round 1
                    :initative initative
                    :remaining initative})))

(defn assess-damage-> [game target-id {:keys [damage] :as outcome}]
  (if damage
    (update-in game [:zoo target-id :hit-points] - damage)
    game))

(defn setup-next-turn-> [{:keys [combat] :as game}]
  (let [{:keys [round initative remaining]} combat
        rest-remaining (rest remaining)
        more? (seq rest-remaining)]
    (when (not more?)
      (println "Round" round "over"))
    (cond-> game
        (not more?) (update-in [:combat :round] inc)
        :true (assoc-in  [:combat :remaining] (if more? rest-remaining initative)))))

(defn dead? [creature]
  (not (pos? (hit-points creature))))

(defn invalid-target? [zoo attacker-id target-id]
  (or (= attacker-id target-id)
      (dead? (zoo target-id))))

(defn take-turn-> [{:keys [combat zoo] :as game}]
  (let [{:keys [remaining initative]} combat
        attacker-id (first remaining)
        attacker (get zoo attacker-id)
        targets (drop-while (partial invalid-target? zoo attacker-id) (shuffle initative))
        target-id (first targets)
        target (get zoo target-id)
        outcome (attack attacker target)]
    (println (str outcome))
    (-> game
        (assess-damage-> target-id outcome)
        setup-next-turn->)))

#_(-> game-state
    begin-combat->
    take-turn->
                take-turn->)


#_(defn play-game 
  "Create the initial game state."
  []
  (let [{:keys [catalog] :as context} (->AppContext (prototype-catalog))
        adventurer (create-monster (:adventurer catalog) [0 0])
        state (default-start-state context adventurer)]
    (doseq [s (take-while running? (iterate iteration  state )) ]
      (doseq [msg (messages s)]
        (println msg)))))
              
#_(defn -main []
  (inspect/start)
  (play-game)
;  (inspect/stop)
;  ( javafx.application.Platform/exit)
  )
