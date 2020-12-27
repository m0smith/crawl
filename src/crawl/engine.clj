(ns crawl.engine
  (:require [clojure.core.async :as async ]
            [com.matthiasnehlsen.inspect :as inspect :refer [inspect]]
            [crawl.state :refer :all]
            [crawl.client.data :refer :all]
            [crawl.combat :refer :all]
            [crawl.dice :refer :all]
            [crawl.monster :refer :all]))



(defn monster-data-send
  ([msg monster]
     (inspect :monster-data-send/msg {:class (class msg) :msg msg :m1 monster})
     (async/>!! (data-channel monster) msg))
  ([msg monster m2]
     (inspect :monster-data-send/msg {:class (class msg) :msg msg :m1 monster :m2 m2})


     (async/>!! (data-channel monster) msg)
     (async/>!! (data-channel m2) msg)))




(defn combat-mode-mesage [state init monster]
  (if (= init 0)
    (message-add-> state "you attack the " (:type monster)) 
    (message-add-> state "the " (:type monster) " surpises you"))) 


(defn enter-combat-mode [state]
  (let [ids (catalog-ids state)
        pid (rand-nth ids)
        adventurer (adventurer state)
        adv-monster (monster-for state adventurer)
        new-location (apply-delta (location adv-monster) [1 0])
        monster (create-monster (prototype state pid) new-location)
        init (roll-initiative)
        new-state (-> state 
                      (zoo-append-> monster)
                      (combatants-set-> adventurer (:id monster))
                      (combatants-next-> init)
                      (combat-mode-mesage init monster)
                      (current-mode-> :combat))]
    (monster-data-send (->CreatedMonster new-state (:id monster) monster (:location monster))
                       monster
                       adv-monster)
    new-state))


(defn loc-n-data [state id]
  (let [{:keys [location client] :as monster} (monster-for state id)]
    [ location (:data-channel client)]))

(defn move 
  "Does not validate the mode"
  [state]
  (if (roll-for-monster)
    (enter-combat-mode state)
    (let [adv-id (adventurer state)
          [location data-channel] (loc-n-data state adv-id)
          rtnval (-> state
                     (monster-move-> adv-id [1 0])
                     (message-add-> "nothing to see here"))
          new-loc (first (loc-n-data rtnval adv-id))]
      (async/>!! data-channel (->Moved state adv-id location new-loc))
      rtnval)))
      



(defn adventurer-wins [state at {:keys [loot type] :as de}]
  (let [at (update-in at [:loot] + loot)]
    (-> state
        (message-add-> (str type " is killed with ~" loot))
        (current-mode-> :move)
        (zoo-append-> at)
        (zoo-append-> de))))

(defn adventurer-loses [state at de]
  (let [moves (moves state)]
    (-> state
        (message-add-> (str "adventurer loses with ~" (:loot de) " in " moves " moves"))
        (zoo-append-> at)
        (zoo-append-> de)
        running->final)))


(defn combat-continues [state at de]
    (-> state
        (zoo-append-> at)
        (zoo-append-> de)
        combatants-next->))

(defn combat* [state at de adv]
  (monster-data-send (->Attacking state at de) at de)
  (let [[stat new-at new-de diff msg] (attack at de)
        state (message-add-> state (str "   " msg))]
    (if (< (:hp new-de) 0)
      (if (= adv (:id new-at))
        (adventurer-wins state new-at new-de)
        (adventurer-loses state new-at new-de))
      (combat-continues state new-at new-de))))


(defn combat
  ""
  [state]
  (let [h 1
        adv (adventurer state)
        [at de] (combatants-as-monsters state)
        {:keys [data-channel command-channel]} (:client at)
        msg (->StartTurn state (:id at))]
    
    (inspect :engine/combat msg)
    (async/>!! data-channel msg)
    (inspect :engine/combat {:msg "Waiting for response"})
    (inspect :engine/combat (async/<!! command-channel))
    (combat* state at de adv)))

(defn close-channels [{:keys [data-channel command-channel]}]
  (when data-channel  (async/close! data-channel))
  (when command-channel  (async/close! command-channel)))

(defn cleanup
  ""
  [state]
  (let [
        adv (adventurer state)
        [at de] (combatants-as-monsters state)]
    (close-channels (:client adv))
    (close-channels (:client at))
    (close-channels (:client de)))
  state)

(defn take-turn [state]
  (let [mode (current-mode state)]
    (condp = mode
      :move (move state)
      :combat (combat state)
      state)))
    

(defn iteration 
  ""
  [state]
  (let [running (running? state)]
    (condp = running
      nil state
      :final (-> state
               running->post-final 
               cleanup)
      (-> state
          pre->
          take-turn
          post->))))


