(ns crawl.ui.javafx
  (:require [fx-clj.core :as fx]))

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


;    (fx/button {:on-action (fn [e] (println "Hello World!"))
;                :text "Click Me 2!"})))

;;(fx/sandbox #'create-view)
