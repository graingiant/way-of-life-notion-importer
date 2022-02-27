(ns user
  (:require [clj-http.client :as client]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [dotenv :refer [env]]))

(def notion-key (env ::NOTION_KEY))

(defn parse-data []
  (with-open [reader (io/reader "way-of-life-data.csv")]
    (doall
     (csv/read-csv reader))))

(defn get-headers [file-data]
  (first file-data))

(first (first (get-headers (parse-data))))