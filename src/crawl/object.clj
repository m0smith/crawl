(ns crawl.object)

(defprotocol Named
  (name-of [o] "Return the name of a thing"))

(defprotocol Attackable
  (hit-points [att] "Return the hit points")
  (armor-class [att] "Return the armor-class"))


