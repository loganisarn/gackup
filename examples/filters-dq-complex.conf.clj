;; GACKUP example config file
;; Task: Read a file and search for utm.gif request with prefix filtering.
;; Also, discard browser strings with iPhone.


;; we will see useful output information on stdout.
(config! *verbose-mode* true)

;; with loganis log format, column 4 is request string.
(config! *prefix-filter* {4 "\"GET /__utm.gif"})

;; string iPhone not in user agent column.
(config! *quick-filter*  {8 #"^((?!iPhone).)*$"})

(config! *log-format* :urchin)
(config! *gackup-file*
	 ["/logs/access.1.gz"])

(config! *mongo-conn*
         "Default MongoDB database info to connect."
         "This will be used when no command line parameter is given."
         {:host "localhost"
          :port 27017
          :user nil
          :pwd nil
          :db  :gackup
          })

