(ns crawl.client.data)

(defrecord Hit [state monster-id])
(defrecord Miss [state monster-id])
(defrecord StartTurn [state monster-id])
