(ns crawl.monster
  (:require [clojure.core.async :as async ]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [crawl.client :as client]
            [crawl.dice :refer :all]
            [crawl.state :refer :all]
            [crawl.client.data :refer :all]
            [crawl.client.command :refer :all]
            [crawl.client.protocol :refer :all]
            [crawl.ui.javafx :refer [javafx-ui]]
            [com.matthiasnehlsen.inspect :as inspect :refer [inspect]]
            )
  (:import [crawl.client.data Attacking StartTurn CreatedMonster]))



(defrecord MonsterPrototype [pid type ac max-hp attack-dice damage-dice loot image])

(defrecord Monster [id pid type ac max-hp hp attack-dice damage-dice loot image client location])


(extend-protocol DataChannel
  StartTurn
  (process-data [{:keys [monster-id state] :as data}]
    (let [{:keys [pid client] :as monster} (monster-for state monster-id)
          {:keys [command-channel]} client
          ;_ (println "process-data: " pid command-channel monster)
          delta (if (= :adventurer pid) [1 0] [-1 0]) ]
      (async/go
        (async/<! (async/timeout 250))
        (async/>! command-channel (->Move monster-id delta)))))
  Object
  (process-data [{:keys [state at-monster de-monster] :as data}]
    (inspect :monster/Attacking data)
    ))
    

(defn simple-ai-client 
  "Return a Client record."
  [monster-id pid]
  (let [{:keys [data-channel command-channel] :as rtnval} (client/create-client)]
    (async/go-loop []
      (let [data (async/<! data-channel)]
        (when data
          (inspect :simple-ai-client/data {:data data :class (class data)})
          (process-data data)
          (recur))))
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


(defn location [monster]
  (inspect :monster/location monster)
  (:location monster))

(defn data-channel [monster]
  (-> monster :client :data-channel))

(defn create-monster 
  "Create an instance of a monster from a given MonstorPrototype"
  [{:keys [pid type ac max-hp attack-dice damage-dice loot image] :as prototype} location]
   (let [hp (throw-dice max-hp)
         monster-id (keyword (gensym (str type "-")))
         client (if (= pid :adventurer) (javafx-ui monster-id pid) (simple-ai-client monster-id pid))
         rtnval (->Monster monster-id
                           pid
                           type
                           (throw-dice ac)
                           hp
                           hp
                           attack-dice
                           damage-dice
                           (throw-dice loot)
                           image
                           client
                           location)]
     rtnval))

              
              
