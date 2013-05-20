;;   Copyright 2013 iWebMa Ltd.

;;   Licensed under the Apache License, Version 2.0 (the "License");
;;   you may not use this file except in compliance with the License.
;;   You may obtain a copy of the License at

;;       http://www.apache.org/licenses/LICENSE-2.0

;;   Unless required by applicable law or agreed to in writing, software
;;   distributed under the License is distributed on an "AS IS" BASIS,
;;   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;;   See the License for the specific language governing permissions and
;;   limitations under the License.

(in-ns 'gackup.core)

(comment
  (def params-status200 {:prefix-filter {5 "200"}})
  (def params-status404 {:prefix-filter {5 "404"}})
  
  (def params-utmgif    {:prefix-filter {4 "\"GET __utm.gif"}})
  (def params-nocookie  {:prefix-filter {10 "-"}})
  (def params-localserv {:prefix-filter {0 "127.0.0.2"}})
  
  (defn program-default-params
    "Returns program default parameters from various default sources:
     variables from cfg file, environmental vals and default values."
    []
    {:files []
     :prefix-filter (or (if-let [x (resolve 'prefix-filter)] @x) {})
     :quick-filter  (or (if-let [x (resolve 'quick-filter)] @x)  {})
     :detail-filter (or (if-let [x (resolve 'detail-filter)] @x) {})
     :chunk-size    (or (if-let [x (resolve 'chunk-size)]   @x) 201)
     :mongo-db      (or (if-let [x (resolve 'mongodb)] @x) (get-env "MONGODB") :gackup)
     })
  )

(defn get-log-key
  [i]
  (cond
   (number? i) (:s (fetch-one log-keymap-name :where {:i i}))
   (coll? i)   (map get-log-key i)))

(defn get-log-keyid
  [i]
  (:i (fetch-one log-keymap-name :where {:s i})))

(defn get-log-keys
  ([] (get-log-keys 25))
  ([l] (fetch log-keymap-name :limit l)))

(defn research-mongo! [] (mongo! :db :gackup))







