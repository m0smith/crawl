(ns crawl.core
  (:require [crawl.monster :refer :all]
            [crawl.context  :refer :all]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn play-game []
  (let [context (->AppContext (prototype-catalog))]
    context))
