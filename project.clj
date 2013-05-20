(defproject gackup "0.1.3"
  :description "Gentle Analytics Backup utility using MongoDB, Clojure and Incanter"
  :url "http://loganis-data-science.blogspot.com"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :scm {:name "git" :url "https://github.com/loganisarn/gackup"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-time "0.5.0"]
                 [incanter "1.4.1"]
                 [org.clojure/tools.cli "0.2.2"]
  :main gackup.core
  :aot :all
  )
