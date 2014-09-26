(ns crawl.core
  (:require [crawl.monster :refer :all]
            [crawl.state :refer :all]
            [crawl.engine :refer :all]
            [crawl.context  :refer :all]))

(defn play-game 
  "Create the initial game state."
  []
  (let [{:keys [catalog] :as context} (->AppContext (prototype-catalog))
        adventurer (create-monster (:adventurer catalog))
        state (default-start-state context adventurer)]
    (take 10 (take-while running? (iterate iteration (merge state {:hp 5}))))))
