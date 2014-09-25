(ns crawl.core
  (:require [crawl.monster :refer :all]
            [crawl.state :refer :all]
            [crawl.context  :refer :all]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn play-game []
  (let [{:keys [catalog] :as context} (->AppContext (prototype-catalog))
        adventurer (create-monster (:adventurer catalog))
        a-id (:id adventurer)
        zoo {a-id adventurer}
        state (->GameState context true :move nil zoo a-id)]
    state))
