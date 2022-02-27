(ns user
  (:require [clj-http.client :as client]
            [clojure.string :as str]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [dotenv :refer [env]]))

(def notion-key (str "Bearer " (env :NOTION_KEY)))

(def notion-database-id (env :NOTION_DB))

(defn db-query-url [] (str/replace "https://api.notion.com/v1/databases/:db_id/query" #":db_id" notion-database-id))

(def notion-page {:parent {:type "page_id"
                           :page_id "4cba2794330d42b48f79738edc82cad7"}})

(def notion-headers {:headers {:Notion-Version "2022-02-22" :Authorization notion-key}
                     :form-params {:page_size 100}
                     :content-type :json
                     :accept :json})


(defn parse-data []
  (with-open [reader (io/reader "way-of-life-data.csv")]
    (doall
     (csv/read-csv reader))))

(defn get-headers [file-data]
  (first file-data))

(first (first (get-headers (parse-data))))

(get (client/post (db-query-url) notion-headers) :body)
