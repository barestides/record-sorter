(ns record-sorter.parser
  (:require [clojure.string :as string]
            [clj-time.format :as f]
            [faconne.core :as faconne]
            [record-sorter.util :as util]))

(defonce records-db (atom []))

(defn parse-file
  [file delimiter-pattern]
  (let [string-records (string/split-lines (slurp file))
        vector-records (map #(string/split % delimiter-pattern) string-records)]
    (faconne/transform vector-records
                       [[lname fname gender color bday]]
                       [{:last-name lname
                         :first-name fname
                         :gender gender
                         :color color
                         :birthday bday}])))

(defn records-by-gender
  []
  (sort-by (juxt :gender :last-name) @records-db))

(defn records-by-birthdate
  []
  (sort-by #(f/parse util/date-formatter (:birthday %)) @records-db))

(defn records-by-lastname-descending
  []
  (sort-by :last-name #(compare %2 %1) @records-db))

(defn parse-record-files-and-add-to-db!
  [pipe-filename comma-filename space-filename]
  (let [records (concat (parse-file pipe-filename #" \| ")
                        (parse-file comma-filename #", ")
                        (parse-file space-filename #" "))]
    (reset! records-db records)))
