(ns crawl.dice)

(def default-sides 20)

(defn roll-die 
  "Roll a single dice and get a number between 1 and the number of
  sides passed.  "
  ([] (roll-die default-sides))
  ([sides] (inc (rand-int sides))))
  
(defn roll-dice
  "Roll the number of dice and return the results as a seq.  If sides
  is not passed, default-sides is used."
  ([num] (roll-dice num default-sides))
  ([num sides] (take num (repeatedly #(roll-die sides)))))
		
