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
        state (default-start-state context adventurer)]
    state))
