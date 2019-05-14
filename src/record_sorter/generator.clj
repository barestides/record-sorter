(ns record-sorter.generator
  (:require [clojure.java.io :as io]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [record-sorter.util :as util]))

(defn rand-birth-date
  [time-before]
  (->> time-before
       rand
       long
       c/from-long
       (f/unparse util/date-formatter)))

(defn gen-record
  []
  (let [last-names ["Arestides" "Johnson" "Smith" "Berry" "Barclay" "Jameson" "Francis" "Carson"]
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
                       :delimiter " "}]
        record-file-dir (io/file "record-files/")]
    (when (not (.exists record-file-dir))
      (.mkdir record-file-dir))
    (doseq [{:keys [file-name delimiter]} file-configs] (let [records (repeatedly records-per-file gen-record)
            str-records (map (comp (partial apply str)
                                   (partial interpose delimiter))
                             records)]
        (spit (io/file record-file-dir file-name) (apply str (interpose "\n" str-records)))))))
