(ns record-sorter.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [faconne.core :as faconne])
  (:gen-class))

(def date-formatter (f/formatter "MM/dd/yyyy"))

(defn rand-birth-date
  [time-before]
  (->> time-before
       rand
       long
       c/from-long
       (f/unparse date-formatter)))

(defn gen-record
  []
  (let [last-names ["Arestides" "Johnson" "Smith" "Berry" "Barclay" "Jameson"]
        first-names ["Alex" "Chris" "Jordan" "Riley" "Elliot"]
        genders ["Male" "Female"]
        colors ["Grey" "Red" "Blue" "Purple" "Yellow" "Chartreuse"]
        time-before (System/currentTimeMillis)]
    (conj (mapv rand-nth [last-names first-names genders colors])
          (rand-birth-date time-before))))

(defn gen-input-files
  [records-per-file]
  (let [file-configs [{:file-name "pipe-delimited"
                       :delimiter " | "}
                      {:file-name "comma-delimited"
                       :delimiter ", "}
                      {:file-name "space-delimited"
                       :delimiter " "}]]
       (doseq [{:keys [file-name delimiter]} (vals file-configs)]
         (let [records (repeatedly records-per-file gen-record)
               str-records (map (comp (partial apply str)
                                      (partial interpose delimiter))
                                records)]
           (spit (io/file "resources" file-name) (apply str (interpose "\n" str-records)))))))

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
  (sort-by #(f/parse date-formatter (:birthday %)) @records-db))

(defn records-by-lastname-descending
  []
  (sort-by :last-name #(compare %2 %1) @records-db))

(defn parse-record-files-and-add-to-db!
  [pipe-filename comma-filename space-filename]
  (let [records (concat (parse-file pipe-filename #" \| ")
                        (parse-file comma-filename #", ")
                        (parse-file space-filename #" "))]
    (reset! records-db records)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
