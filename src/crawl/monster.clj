(ns crawl.monster
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [crawl.dice :refer :all]))



(defrecord MonsterPrototype [pid type ac max-hp attack-dice damage-dice loot])

(defrecord Monster [id pid type ac max-hp hp attack-dice damage-dice loot])


(defn prototype-catalog 
  "Create and return the catalog of monster prototypes.  The catalog is a map of
`pid -> MonsterPrototype`."
  []
  (let [vals (->> "monsters.edn"
                  io/resource
                  slurp
                  edn/read-string
                  (map #(apply ->MonsterPrototype %)))]
    (zipmap (map :pid vals) vals)))


(defn create-monster 
  "Create an instance of a monster from a given MonstorPrototype"
  [{:keys [pid type ac max-hp attack-dice damage-dice loot] :as prototype}]
   (let [hp (throw-dice max-hp)]
     (->Monster (keyword (gensym (str type "-")))
                pid
                type
                (throw-dice ac)
                hp
                hp
                attack-dice
                damage-dice
                (throw-dice loot))))
              
              
