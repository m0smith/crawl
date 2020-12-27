(ns crawl.creature
  (:require
   [crawl.object :as object]))

(defrecord Rat []
    crawl.object.CrawlObject)

