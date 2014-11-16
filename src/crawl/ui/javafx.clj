(ns crawl.ui.javafx
  (:require [fx-clj.core :as fx]
            [clojure.core.async :as async ]
            [crawl.state :refer :all]
            [crawl.client :as client]
            [crawl.client.data :refer :all]
            [crawl.client.command :refer :all]
            [com.matthiasnehlsen.inspect :as inspect :refer [inspect]]
            [crawl.client.protocol :refer :all])
  (:import [crawl.client.data StartTurn]))


(extend-protocol DataChannel
  StartTurn
  (process-data [{:keys [monster-id state] :as data}]
    (let [{:keys [pid client] :as monster} (monster-for state monster-id)
          {:keys [command-channel]} client
          ;_ (println "process-data: " pid command-channel monster)
          delta (if (= :adventurer pid) [1 0] [-1 0]) ]
      (async/go
        (async/>! command-channel (->Move monster-id delta))))))



(defn floor [id]
  (fx/image-view (keyword (str "#floor-" id))
                 {
                  :on-mouse-clicked (fn [e] (println "Hello World!" id (bean e)))
                  :fit-height 32
                  :fit-width 32
                  }
                 (javafx.scene.image.Image. "images/Background/Textures/FlagsMid.jpg")))

(defn hero [id]
  (fx/image-view (keyword (str "#floor-" id))
                 {
                  :on-mouse-clicked (fn [e] (println "he he he!" id (bean e)))
                  :fit-height 32
                  :fit-width 32
                  }
                 (javafx.scene.image.Image. "images/Creatures/knight_concept_by_quintuscassius-d4xbniz.jpg")))

(defn create-view []
  (apply fx/tile-pane :#board { :pref-columns 10 }
                (hero 0) 
                (map floor (drop 1 (range 100)))))



(defn javafx-ui 
  "Return a Client record."
  [monster-id pid]
  (fx/sandbox #'create-view)
  (let [{:keys [data-channel command-channel] :as rtnval} (client/create-client)]
    (async/go
      (loop [data (async/<! data-channel)]
        (when data
          (inspect :javafx/data {:msg "data recived"})
          (process-data data)
          (recur (async/<!  data-channel)))))
    rtnval))
  

;    (fx/button {:on-action (fn [e] (println "Hello World!"))
;                :text "Click Me 2!"})))

;;(fx/sandbox #'create-view)
