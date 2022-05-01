(ns crawl.random)

(defn random-in-range
  "Return a random integer in the range `start` (inclusive) to `end` (inclusive)"
  [start end]
  (+ start (rand-int (inc (- end start)))))


