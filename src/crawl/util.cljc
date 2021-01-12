(ns crawl.util
  #?(:cljs (:refer-clojure :exclude [uuid])))

(defn uuid
  "Return a uuid.

  When called with no args, generate a random.

  When called with a string, convert it to a uuid or throw and exception
  When called with a uuid, return it."
  ([] #?(:clj (java.util.UUID/randomUUID)
         :cljs (random-uuid)))
  ([v] (cond
         (string? v) #?(:clj (java.util.UUID/fromString v)
                        :cljs (clojure.core/uuid v))
         (uuid? v) v)))
