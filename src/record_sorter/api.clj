(ns record-sorter.api
  (:require [org.httpkit.server :as httpkit]
            [cheshire.core :as chesh]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [record-sorter.parser :as parser]))

(defroutes parser-routes
  (POST "/records" [] (fn [req] (parser/add-record (get-in req [:params :record]))))
  (GET "/records/gender" [] (fn [_] (chesh/generate-string (parser/records-by-gender))))
  (GET "/records/birthdate" [] (fn [_] (chesh/generate-string (parser/records-by-birthdate))))
  (GET "/records/name" [] (fn [_] (chesh/generate-string (parser/records-by-lastname-descending)))))
