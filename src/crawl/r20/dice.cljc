(ns crawl.r20.dice
  (:require [crawl.random :refer :all]))

(defprotocol Roller
  (roll [d] "Roll the bones"))

(defn n-sided-dice-roller [sides]
  (fn [& _] {:value (random-in-range 1 sides)
             :min 1
             :max sides
             }))

(defrecord DiceDefinition [sides quantity modifier]

  Roller
  (roll [d] (let [rolls (map (n-sided-dice-roller sides) (range quantity))]
              {:value (apply + modifier (map :value rolls))
               :min (reduce + (map :min rolls))
               :max (reduce + (map :max rolls))
               :rolls rolls
               :modifier modifier})))

(defn define-dice
  ([sides] (->DiceDefinition sides 1 0))
  ([sides quantity] (->DiceDefinition sides quantity 0))
  ([sides quantity modifier] (->DiceDefinition sides quantity modifier)))


