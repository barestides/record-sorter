(ns record-sorter.core
  (:require [clojure.java.io :as io]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c])
  (:gen-class))

(def date-formatter (f/formatter "MM/dd/yyyy"))
(def file-configs [{:file-name "pipe-delimited"
                    :delimiter " | "}
                   {:file-name "comma-delimited"
                    :delimiter ", "}
                   {:file-name "space-delimited"
                    :delimiter " "}])

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
  (doseq [{:keys [file-name delimiter]} file-configs]
    (let [records (repeatedly records-per-file gen-record)
          str-records (map (comp (partial apply str)
                                 (partial interpose delimiter))
                           records)]
      (spit (io/file "resources" file-name) (apply str (interpose "\n" str-records))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
