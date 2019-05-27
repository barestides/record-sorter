(ns record-sorter.parser-test
  (:require [clojure.test :refer :all]
            [record-sorter.parser :refer :all]))

(deftest parser-test
  (testing "Record file parser"
      (let [pipe-filename "test/resources/pipe-delimited"
            comma-filename "test/resources/comma-delimited"
            space-filename "test/resources/space-delimited"
            expected-db '({:last-name "Jameson",
                           :first-name "Elliot",
                           :gender "Male",
                           :color "Chartreuse",
                           :birthday "03/06/2005"}
                          {:last-name "Smith",
                           :first-name "Jordan",
                           :gender "Female",
                           :color "Blue",
                           :birthday "09/21/1988"}
                          {:last-name "Francis",
                           :first-name "Chris",
                           :gender "Male",
                           :color "Yellow",
                           :birthday "05/17/1981"}
                          {:last-name "Jameson",
                           :first-name "Elliot",
                           :gender "Male",
                           :color "Purple",
                           :birthday "08/15/1997"}
                          {:last-name "Smith",
                           :first-name "Elliot",
                           :gender "Male",
                           :color "Yellow",
                           :birthday "12/02/1997"}
                          {:last-name "Barclay",
                           :first-name "Jordan",
                           :gender "Female",
                           :color "Blue",
                           :birthday "04/13/2000"})]
        (parse-record-files-and-add-to-db! pipe-filename comma-filename space-filename)
        (is (= expected-db @records-db)))))

(deftest sorting-test
  (testing "Sorting"
    ;;do a with-redefs for easier testing? probably
    (let [sample-db (atom '({:last-name "Jameson",
                             :first-name "Elliot",
                             :gender "Male",
                             :color "Chartreuse",
                             :birthday "03/06/2005"}
                            {:last-name "Smith",
                             :first-name "Jordan",
                             :gender "Female",
                             :color "Blue",
                             :birthday "09/21/1988"}
                            {:last-name "Francis",
                             :first-name "Chris",
                             :gender "Male",
                             :color "Yellow",
                             :birthday "05/17/1981"}))]
      (with-redefs [records-db sample-db]
        (testing "Sort by Gender"
          (let [sorted-by-gender (records-by-gender)]
            (is (= (:first-name (first sorted-by-gender)) "Jordan") "Females should be before males")
            (is (= (:first-name (last sorted-by-gender)) "Elliot")
                "Same gender records should be sorted by last name.")))

        (testing "Sort by Birthdate"
          (let [[first-name second-name third-name] (map :first-name (records-by-birthdate))]
            (is (and (= first-name "Chris") (= second-name "Jordan") (= third-name "Elliot")))))

        (testing "Sort by Last Name Descending")
        (let [[first-name second-name third-name] (map :first-name (records-by-lastname-descending))]
          (is (and (= first-name "Jordan") (= second-name "Elliot") (= third-name "Chris"))))))))
