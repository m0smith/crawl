(ns crawl.client.data)

(defrecord Attacking [state at-monster de-monster])
(defrecord Attacked  [state at-monster de-monster result diff])
(defrecord StartTurn [state monster-id])
(defrecord CreateMonster [state monster-id])
(defrecord Moved [state monster-id old-location new-location])
