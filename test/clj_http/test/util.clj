(ns clj-http.test.util
  (:use [clj-http.util]
        [name.stadig.conjecture]))

(deftest test-lower-case-keys
  (are [map expected]
    (is (= expected (lower-case-keys map)))
    nil nil
    {} {}
    {"Accept" "application/json"} {"accept" "application/json"}
    {"X" {"Y" "Z"}} {"x" {"y" "Z"}}))
