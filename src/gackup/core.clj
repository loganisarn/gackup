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

(ns gackup.core
  (:gen-class)
  (:refer-clojure)
  (:require [clj-time.core])
  (:require [clj-time.format])
  (:require [clj-time.coerce])
  (:use [incanter io core stats charts datasets mongodb  ])
  (:use [clojure.tools.cli :only [cli]])
  (:import
   [java.io
    File FileInputStream FileOutputStream
    BufferedReader BufferedWriter
    InputStreamReader OutputStreamWriter]
   [java.util.zip
    GZIPInputStream GZIPOutputStream]))

(use 'somnium.congomongo)
(use 'incanter.mongodb)

(dorun (map load ["def" "util" "config" "format" "db" "model" "research"]))

(defn -main
  "Gentle Analytics Backup - a log processing utility"
  [& args]
  (let [cli* [["-f" "--file" "Input log file"]
              ["-d" "--directory" "Input log directory"]
              ["-Q" "--quick-filter" "Quick prefix filter for column string"
               :parse-fn read-string]
              ["-R" "--detail-filter" "Regex filter for column string"
               :parse-fn read-string]
              ["-F" "--prefix-filter" "Filter map for computed values"
               :parse-fn read-string]
              ["-O" "--out" "Output type" :parse-fn keyword]
              ["-g" "--logformat" "Log file format" :parse-fn keyword :default nil]
              ["-s" "--chunk-size" "Number of lines in a chunk for parallel" :parse-fn string-to-int]
              ["-c" "--config" "Configuration file location" :default nil]
              ["-v" "--verbose" "Turn on verbose mode" :default nil :flag true]
              ["-h" "--help" "Displays help information" :default false :flag true]]
        
        [options cli-args cli-banner] (apply cli args cli*)
        
        verbose* (options :verbose *verbose-mode*)

        _  (if-let [cfg (options :config)] (try
                                             (binding [*ns* (the-ns 'gackup.core)]
                                               (if (load-file cfg)
                                                 (if verbose* (println "* cfg file loaded:" cfg))
                                                 (if verbose* (println "* could not load cfg file: " cfg)))
                                               (catch RuntimeException e
                                                 (do (println "* error in config file: " (.getMessage e))
                                                     (.printStackTrace e)))))
                   (println "* no config file specified."))        
        
        fnames  (concat
                 (if-let [infile (options :file)]
                   (if (.startsWith infile "[")
                     (read-string infile)
                     (vector infile)))
                 (if-let [indir (options :directory)]
                   (->>
                    (into [] (.list (File. indir)))
                    (remove #(or (.startsWith % ".") (.startsWith % "#") (.endsWith % "#") (.endsWith % "~")))
                    (map #(.getAbsolutePath (File. indir %))))))
        
        runparams { :file   (or (seq fnames) *gackup-file*)
                   :qfilter (options :quick-filter)
                   :mfilter (options :detail-filter)
                   :pfilter (options :prefix-filter)
                   :csize   (options :chunk-size)
                   :mongo-db   (or (options :mongodb)   (get-env "MONGODB"))
                   :web true
                   :format  (options :logformat)
                   :verbose (options :verbose *verbose-mode*)
                   :output  (options :out *gackup-output*)}]
    (use 'gackup.core)
    (println "Gackup - Gentle Analytics Backup - log processing utility")
    (cond
     (or (nil? args ) (options :help)) (do (println cli-banner))
     :default
     (do
       (gackup-program runparams)
       (shutdown-agents)))))

