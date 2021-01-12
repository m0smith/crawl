(ns crawl.object)

(defprotocol Named
  (name-of [o] "Return the name of a thing"))

(defprotocol Identifiable
  (id-of [o] "Return the unique id of a thing, maybe a uuid"))

(defprotocol Attackable
  (hit-points [att] "Return the hit points")
  (armor-class [att] "Return the armor-class"))


