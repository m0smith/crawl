(ns crawl.state)

(defrecord GameState [context running mode combatants zoo adventurer] )

(defn default-start-state [context adventurer]
  (->GameState context true :move nil { (:id adventurer) adventurer } (:id adventurer)))


(defn running? [{:keys [running]}]
  running)
  
(defn running->post-final [state]
  (merge state {:running nil}))
