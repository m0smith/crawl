(ns crawl.client
  (:require [clojure.core.async :as async ]))

(defrecord Client [client-id data-channel command-channel])

(defn create-client []
  (->Client (gensym "client_") (async/chan) (async/chan)))
