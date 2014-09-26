(ns crawl.engine
  (:require [crawl.state :refer :all]))


(defn take-turn [state]
  (let [hp (- (:hp state) 1)]
    (if (> hp 0)
      (merge state {:hp hp})
      (merge state {:running :final :hp 0}))))

(defn iteration 
  ""
  [state]
  (let [running (running? state)]
    (condp = running
      nil state
      :final (running->post-final state)
      (take-turn state))))

