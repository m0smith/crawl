(ns crawl.monster
  (:require
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   
   [crawl.creature :refer [->CreatureType ->Creature Attacker attack-dice damage-dice]]
   [crawl.object :refer [Attackable Named armor-class name-of hit-points]]
   [crawl.util :refer [uuid]]
   ))

;;(def Rat (->CreatureType "Rat" 10 [1 20 1] [1 4 0]))
;;(def Snake (->CreatureType "Snake" 10 [1 20 1] [1 4 0]))

(defn load-monster [name]
  (let [url (str "https://www.dnd5eapi.co/api/monsters/" name "/")]
    (delay
      (json/read (io/reader url) :key-fn keyword))))

(defn get-damage-dice [rec]
  (-> rec
      :actions
      first
      :damage
      first
      :damage_dice))

(defn get-attack-dice [rec]
  (let [bonus (-> rec
                  :actions
                  first
                  :attack_bonus)]
    [1 20 bonus]))

(defrecord Dnd5EAPIMonster [rec]
  Named
  (name-of [m] (:name @rec))

  Attacker
  (attack-dice [m] (get-attack-dice @rec))
  (damage-dice [m] (get-damage-dice @rec))

  Attackable
  (armor-class [m] (:armor_class @rec))
  (hit-points [m] (:hit_points @rec)))


(defn dnd5e-api-monster [name]
  (->Dnd5EAPIMonster (load-monster name)))

(def Rat (dnd5e-api-monster "rat"))
(def Snake (dnd5e-api-monster "constrictor-snake"))

#_(defrecord MonsterPrototype [pid type ac max-hp attack-dice damage-dice loot image])

#_(defrecord Monster [id pid type ac max-hp hp attack-dice damage-dice loot image client location])


#_(extend-protocol DataChannel
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
    

#_(defn simple-ai-client 
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
  

#_(defn prototype-catalog 
  "Create and return the catalog of monster prototypes.  The catalog is a map of
`pid -> MonsterPrototype`."
  []
  (let [vals (->> "monsters.edn"
                  io/resource
                  slurp
                  edn/read-string
                  (map #(apply ->MonsterPrototype %)))]
    (zipmap (map :pid vals) vals)))


#_(defn location [monster]
  (inspect :monster/location monster)
  (:location monster))

#_(defn data-channel [monster]
  (-> monster :client :data-channel))

#_(defn create-monster 
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

              
              
