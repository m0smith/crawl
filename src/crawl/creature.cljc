(ns crawl.creature
  (:require
   [crawl.object :refer [Attackable Named Identifiable armor-class id-of]]
   [crawl.util :refer [uuid]]))

(defprotocol Attacker
  (attack-dice [att] "Return the attack DiceSpec")
  (damage-dice [att] "Return the damage DiceSpec"))

(defrecord CreatureType [type armor-class attack-dice damage-dice]
  Named
  (name-of [c] (:type c))
  
  Attacker
  (attack-dice [c] (:attack-dice c))
  (damage-dice [c] (:damage-dice c))

  Attackable
  (armor-class [c] (:armor-class c)))

(defrecord Creature [id name creature-type hit-points]
  Named
  (name-of [c] (:name c))

  Identifiable
  (id-of [c] id)
  
  Attacker
  (attack-dice [c] (attack-dice creature-type))
  (damage-dice [c] (damage-dice creature-type))

  Attackable
  (hit-points [c] (:hit-points c))
  (armor-class [c] (armor-class creature-type)))

(defn ->zoo [creatures]
  (into {} (zipmap (map id-of creatures) creatures)))
