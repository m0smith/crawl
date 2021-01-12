(ns crawl.event)

(defprotocol EventProtocol
  (short-description [e] "The short description")
  (game-state [e])
  (timestamp [e])
  (sequence-number [e]))


(defrecord Event [timestamp game-state desc sequence-number]
  EventProtocol
  (timestamp [e] (:timestamp e))
  (game-state [e] (:game-state e))
  (short-description [e] (:desc e))
  (sequence-number [e] (:sequence-number e))
  )





