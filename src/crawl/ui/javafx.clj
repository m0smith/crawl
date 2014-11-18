(ns crawl.ui.javafx
  (:require [fx-clj.core :as fx]
            [clojure.core.async :as async ]
            [crawl.state :refer :all]
            [crawl.client :as client]
            [crawl.client.data :refer :all]
            [crawl.client.command :refer :all]
            [com.matthiasnehlsen.inspect :as inspect :refer [inspect]]
            ;[crawl.client.protocol :refer :all]
            )
  (:import [crawl.client.data Attacking StartTurn]))


(defprotocol JavaFxDataChannel
  (process-data [data]))


    


(defn to-id [id]
  (keyword (str "#floor-" id)))

(defn floor [id]
  (fx/image-view (to-id id)
                 {
                  :on-mouse-clicked (fn [e] (println "Hello World!" id (bean e)))
                  :fit-height 32
                  :fit-width 32
                  }
                 (javafx.scene.image.Image. "images/Background/Textures/FlagsMid.jpg")))

(defn hero [id]
  (fx/image-view (to-id id)
                 {
                  :on-mouse-clicked (fn [e] (println "he he he!" id (bean e)))
                  :fit-height 32
                  :fit-width 32
                  }
                 (javafx.scene.image.Image. "images/Creatures/knight_concept_by_quintuscassius-d4xbniz.jpg")))

(def floor-tiles-pairs (map #(vector (to-id %1) (floor %1)) (range 100)))
  
(def floor-tiles (map second floor-tiles-pairs))
  
(def floor-tiles-map (reduce conj {} floor-tiles-pairs))

(defn create-view []
  (apply fx/tile-pane :#board { :pref-columns 10 }
         floor-tiles))


(extend-protocol JavaFxDataChannel
  StartTurn
  (process-data [{:keys [monster-id state] :as data}]
    (let [{:keys [pid client] :as monster} (monster-for state monster-id)
          {:keys [command-channel]} client
          ;_ (println "process-data: " pid command-channel monster)
          delta (if (= :adventurer pid) [1 0] [-1 0]) ]
      (async/go
        (async/>! command-channel (->Move monster-id delta)))))

  Attacking
  (process-data [{:keys [state at-monster de-monster] :as data}]
    (inspect :javafx/Attacking data)
    (async/go
      (fx/run<!
       (fx/pset! (floor-tiles-map (to-id (-> at-monster :location first)))
                 (javafx.scene.image.Image. (-> at-monster :image)))))))
  
(defn javafx-ui 
  "Return a Client record."
  [monster-id pid]
  (inspect :javafx/floor-tiles-map floor-tiles-map)
  (fx/sandbox #'create-view)
  (let [{:keys [data-channel command-channel] :as rtnval} (client/create-client)]
    (async/go-loop []
      (let [data (async/<! data-channel)]
        (when data
          (inspect :javafx/floor-tiles-map floor-tiles-map)
          (inspect :javafx-ui/data {:msg "data recived" :data data :class (class data)
                                    })
          (process-data data)
          (recur))))
    rtnval))
  

;    (fx/button {:on-action (fn [e] (println "Hello World!"))
;                :text "Click Me 2!"})))

;;(fx/sandbox #'create-view)
