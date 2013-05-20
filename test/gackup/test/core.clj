(ns gackup.test.core
  (:use [gackup.core])
  (:use [clojure.test]))

;;(run-all-tests)

(deftest test-integer-pattern
  (is (= "12345" (integer-pattern "+12345")))
  (is (= "-12345" (integer-pattern "-12345")))
  (is (= nil (integer-pattern "number")))
  )

(deftest test-string-to-int
  (is (= 12345 (string-to-int "12345")))
  (is (= -12345 (string-to-int "-12345")))
  (is (= 12345 (string-to-int "+12345")))
  (is (= nil (string-to-int "number")))
  (is (= nil (string-to-int "12345.6")))
  )

(deftest test-string-to-long
  (is (= 12345 (string-to-long "12345")))
  (is (= -12345 (string-to-long "-12345")))
  (is (= 12345 (string-to-long "+12345")))
  (is (= nil (string-to-long "number")))
  (is (= nil (string-to-long "12345.6")))
  )

(deftest test-ip-to-int
  (is (= 1084752129 (ip-to-int "192.168.1.1")))
  )

(deftest test-date-to-int
  (is (= 1360731723 (date-to-int "[13/Feb/2013:01:02:03 -0400]")))
  )

(deftest test-flatten-map
  (is (= {:a 4, :b-x-y 3} (flatten-map {:a 4 :b {:x {:y 3}} })))
  )

(deftest test-map-remove-nils
  (is (= {:a 1, :c 4} (map-remove-nils {:a 1 :b nil :c 4 :d nil})))
  )

(deftest test-trim-borders
  (is (= "bc" (trim-borders "abcd")))
  )

(deftest test-temquote
  (is (= "abc" (remquote "\"abc\"")))
  (is (= "abc" (remquote "abc")))
  )

(deftest test-decompose-referer
  (is (= {:url "http://www.google.jp/", :params {:q "querystring", :hl "en"}}
         (decompose-referer "http://www.google.jp/?hl=en&q=querystring")))
  )

(deftest test-decompose-cookie
  (is (= nil (decompose-cookie "")))
  (is (= nil (decompose-cookie "-")))
  ;; TODO test cookie lines.
  )

(deftest test-decompose-params
  (is (= {} (decompose-params "") (decompose-params nil)))
  (is (= {:one "1"} (decompose-params "one=1")))
  (is (= {:one "1" :two "2"} (decompose-params "one=1&two=2")))
  )

(deftest test-decompose-request
  (is (= {:params "one=1", :method "GET", :url "/something.gif", :protocol "HTTP/1.0"}
         (decompose-request "GET /something.gif?one=1 HTTP/1.0")))
  )


(deftest map-line-matches-filter?
  (is (not (map-matches-filter? {:a #"\d+"} {:a "de"})))
  (is (map-matches-filter? {:a #"\d+"} {:a "d34ed d"}))
  )

