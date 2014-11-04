(ns crawl.client.data)

(defrecord StartTurn [monster-id])
(defrecord Hit [monster-id])
(defrecord Miss [monster-id])
