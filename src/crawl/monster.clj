(ns crawl.monster
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))


(defrecord MonstorPrototype [pid type ac max-hp attack-dice dammage-dice loot])

(defrecord Monstor [id pid type ac max-hp hp attack-dice dammage-dice loot])


(defn prototype-catalog 
  "Create the catalog of monster prototypes."
  []
  (let [vals (->> "monsters.edn"
                  io/resource
                  slurp
                  edn/read-string
                  (map #(apply ->MonstorPrototype %)))]
    (zipmap (map :pid vals) vals)))
