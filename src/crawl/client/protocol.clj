(ns crawl.client.protocol)

(defprotocol DataChannel
  (process-data [data]))

(defprotocol CommandChannel
  (process-command [data]))

