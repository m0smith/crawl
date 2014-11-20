(ns crawl.client.data)

(defrecord Attacking [state at-monster de-monster])
(defrecord Attacked  [state at-monster de-monster result diff])
(defrecord StartTurn [state monster-id])
(defrecord CreatedMonster [state monster-id monster location])
(defrecord Moved [state monster-id old-location new-location])
