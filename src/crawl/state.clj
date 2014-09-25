(ns crawl.state)

(defrecord GameState [context running mode combatants zoo adventurer] )

(defn default-start-state [context adventurer]
  (->GameState context true :move nil { (:id adventurer) adventurer } (:id adventurer)))
