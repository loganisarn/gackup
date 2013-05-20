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

(def apache-log-date-format "[dd/MMM/yyyy:HH:mm:ss Z]")

(def work-history
  "Some statistics on current processing session."
  {:processed-chunks (atom 0)
   :processed-files  (atom [])})

(def utm-gif  "/__utm.gif")

(def log-data-name
  "Default collection name for log data"
  :logdata)

(def log-dict-name
  "Default collection name for log dictionary"
  :logdict)

(def log-keymap-name
  "Def coll name for column names in log-data-name table."
  :logdatakeys)

(def ^:dynamic *insert-if-empty*
  "Insert keys with nil or -1 values to db."
  false)

(def ^:dynamic *gackup-file*
  "The filename or collection of filenames to process by default."
  nil)

(def ^:dynamic *prefix-filter* {})
(def ^:dynamic *quick-filter*  {})
(def ^:dynamic *detail-filter* {})

(def ^:dynamic *chunk-size*    200)
(def ^:dynamic *verbose-mode*  false)

(def ^:dynamic *log-format* :urchin)

(def ^:dynamic *mongo-conn* {:host "localhost"
                             :port 27017
                             :user nil
                             :pwd nil
                             :db  :gackup
                             })

(def ^:dynamic *gackup-output* :mongo)


