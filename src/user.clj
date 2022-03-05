(ns user
  (:require [clj-http.client :as client]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [dotenv :refer [env]]))

(def notion-key (str "Bearer " (env :NOTION_KEY)))

(def notion-database-id (env :NOTION_DB))

(def create-page-url
  "https://api.notion.com/v1/pages")

(def new-entry {:parent {:type "database_id" :database_id notion-database-id}
                :properties {:Date {:title [{:type "text"
                                             :text {:content "The title"}}]}
                             :Writing {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Writing)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Didn’t spend frivolously (3 month goal)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Didn’t spend frivolously (3 month goal))" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Didn’t order out" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Didn’t order out)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Meditating" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Meditating)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Exercising / 10K Steps" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Exercising / 10K Steps)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Time Outdoors (3x week)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Time Outdoors (3x week))" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Community" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Community)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Read" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Read)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Take Vitamins" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Take Vitamins)" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Journaling" {:rich_text [{:type "text" :text {:content "content"}}]}
                             "Notes (Journaling)" {:rich_text [{:type "text" :text {:content "content"}}]}}})

(def notion-headers {:headers {:Notion-Version "2022-02-22" :Authorization notion-key}
                     :form-params {:page_size 100}
                     :content-type :json
                     :accept :json})

(defn is-date-column-value [idx]
  (when (= 0 idx) true))

(defn parse-data []
  (with-open [reader (io/reader "way-of-life-data.csv")]
    (doall
     (csv/read-csv reader))))

(def csv-data (rest (parse-data)))

(def csv-headers (first (parse-data)))

(defn update-title [item template]
  (assoc-in template [:properties :Date :title 0 :text :content] item))

(defn update-rest [item title template]
  (assoc-in template [:properties title :rich_text 0 :text :content] item))

;; create new collection with parsed data using update-template
(defn update-template [row]
  (reduce-kv (fn [template idx row-item] (if (is-date-column-value idx)
                                           (update-title row-item template)
                                           (update-rest row-item (csv-headers idx) template))) new-entry row))

(doseq [v (map update-template csv-data)]
  (let [new-entry-filled (assoc-in notion-headers [:form-params] v)]
    (client/post create-page-url new-entry-filled)
    (Thread/sleep 2000)))
