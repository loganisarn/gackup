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

(def file-name 
  "File name(s) to open on run."
  nil)


(config! *insert-if-empty*
         "Controls if nil or -1 values should be inserted into DB."
         false)

(config! *chunk-size*
         "Default chunk size for parallel processing."
         200)

(config! *quick-filter*
         "Quick filter map. Column name to regex."
         {})

(config! *prefix-filter*
         "Prefix filter for quicker filtering. Map of column number to prefix string."
         {})

(config! *detail-filter*
         "Filter map for detailed filtering. Map of category names to regex."
         {})

(config! *verbose-mode*
         "If true, program prints out debug and progress information."
         true)


(config! *mongo-conn*
         "Default MongoDB database name to connect."
         "This will be used when no command line parameter is given."
         {:host "localhost"
          :port 27017
          :user nil
          :pwd nil
          :db  :gackup
          })

(config! *log-format* :loganis)
