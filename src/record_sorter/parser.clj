(ns record-sorter.parser
  (:require [clojure.string :as string]
            [clj-time.format :as f]
            [faconne.core :as faconne]
            [record-sorter.util :as util]))

(defonce records-db (atom []))

(defn parse-record
  [string-record delimiter-pattern]
  (faconne/transform (string/split string-record delimiter-pattern)
                     [lname fname gender color bday]
                     {:last-name lname
                      :first-name fname
                      :gender gender
                      :color color
                      :birthday bday}))

(defn add-record
  [string-record]
  (let [delimiter-pattern (cond (re-find #"\|" string-record)
                                #" \| "

                                (re-find #", " string-record)
                                #", "

                                (re-find #" " string-record)
                                #" ")
        parsed-record (parse-record string-record delimiter-pattern)]
    (swap! records-db conj parsed-record)))

(defn parse-file
  [file delimiter-pattern]
  (let [string-records (string/split-lines (slurp file))]
    (map #(parse-record % delimiter-pattern) string-records)))

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
