(ns crawl.monster
  (:require [crawl.monster.catalog :refer :all]))


(defrecord MonstorPrototype [pid type ac max-hp attack-dice dammage-dice loot])

(defrecord Monstor [id pid type ac max-hp hp attack-dice dammage-dice loot])


(defn prototype-catalog []
  (let [vals (map #(apply ->MonstorPrototype %) catalog-def)]
    (zipmap (map :pid vals) vals)))
