(ns crawl.monster
  (:require [clojure.core.async :as async ]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [crawl.client :as client]
            [crawl.dice :refer :all]
            ))



(defrecord MonsterPrototype [pid type ac max-hp attack-dice damage-dice loot image])

(defrecord Monster [id pid type ac max-hp hp attack-dice damage-dice loot image client])

(defn simple-ai-client 
  "Return a Client record."
  []
  (let [{:keys [data-channel command-channel] :as rtnval} (client/create-client)]
    (async/go
      (loop [data (async/<!! data-channel)]
        (when data
          (async/>! command-channel :move)
          (recur (async/<! data-channel)))))
    rtnval))
  

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
  [{:keys [pid type ac max-hp attack-dice damage-dice loot image] :as prototype}]
   (let [hp (throw-dice max-hp)
         client (simple-ai-client)]
     (->Monster (keyword (gensym (str type "-")))
                pid
                type
                (throw-dice ac)
                hp
                hp
                attack-dice
                damage-dice
                (throw-dice loot)
                image
                client)))

              
              
