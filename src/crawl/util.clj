(ns crawl.util)

(defn uuid
  "Return a uuid.

  When called with no args, generate a random.

  When called with a string, convert it to a uuid or throw and exception
  When called with a uuid, return it."
  ([] (java.util.UUID/randomUUID))
  ([v] (cond
         (string? v) (java.util.UUID/fromString v)
         (uuid? v) v)))
