(ns record-sorter.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.tools.cli :as cli]
            [record-sorter.parser :as parser])
  (:gen-class))

(defn filename-exists?
  [filename]
  (.exists (io/file filename)))

(def cli-options
  [["-c" "--comma-delimited-file FILE" "Comma-delimited File"
    :default "sample-record-files/comma-delimited"
    :validate [filename-exists?]]
   ["-p" "--pipe-delimited-file FILE" "Pipe-delimited File"
    :default "sample-record-files/pipe-delimited"
    :validate [filename-exists?]]
   ["-s" "--space-delimited-file FILE" "Space-delimited File"
    :default "sample-record-files/space-delimited"
    :validate [filename-exists?]]])

(defn -main
  [& args]
  (let [parsed-args (cli/parse-opts args cli-options)
        {:keys [comma-delimited-file pipe-delimited-file space-delimited-file]}
        (:options parsed-args)]
    (parser/parse-record-files-and-add-to-db!
     pipe-delimited-file comma-delimited-file space-delimited-file)
    (println "Records Sorted by Gender:")
    (pprint/pprint (parser/records-by-gender))
    (println "Records Sorted by Birth Date:")
    (pprint/pprint (parser/records-by-birthdate))
    (println "Records Sorted by Reverse Last Name:")
    (pprint/pprint (parser/records-by-lastname-descending))))
