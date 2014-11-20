(ns crawl.core
  (:require [crawl.monster :refer :all]
            [crawl.state :refer :all]
            [crawl.engine :refer :all]
            [com.matthiasnehlsen.inspect :as inspect :refer [inspect]]
            [crawl.context  :refer :all]))



(defn play-game 
  "Create the initial game state."
  []
  (let [{:keys [catalog] :as context} (->AppContext (prototype-catalog))
        adventurer (create-monster (:adventurer catalog) [0 0])
        state (default-start-state context adventurer)]
    (doseq [s (take-while running? (iterate iteration  state )) ]
      (doseq [msg (messages s)]
        (println msg)))))
              
(defn -main []
  (inspect/start)
  (play-game)
;  (inspect/stop)
;  ( javafx.application.Platform/exit)
  )
