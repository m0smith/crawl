(ns crawl.dice)

(defn roll 
  "Roll a single dice and get a number between 1 and the number of
  sides passed.  The default sides is 20"
  ([] (roll 20))
  ([sides] (inc (rand-int sides))))
  
