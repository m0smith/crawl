(ns crawl.context
  (:use [clojure.algo.monads]))

;; ## Application Context and Dependency Injectin
;;  * catalog - map of monster prototypes: [id -> MonsterPrototype]

(defrecord AppContext [catalog])



(defn apply-ctx [ ctx factory ]
  ((factory) ctx))


(defmacro with-ctx [ & body ]
	`(domonad reader-m ~@body))

(defmacro with-ctx-keys [ ctx & body ]
	`(domonad reader-m ~ [{:keys ctx} (ask)]  ~@body))

(defmacro defnctx [name & body]
  `(defn ~name []
     (with-ctx  ~@body)))
