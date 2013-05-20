;; GACKUP example config file
;; task: read a bunch of files and search for utm.gif request
;; uses prefix filtering for quick delivery.

;; program prints useful and less useful information while running
(config! *verbose-mode* true)

;; default chunk size is 200 lines/chunk.
(config! *chunk-size* 200)

;; column nr. 4 starts with the following request string.
(config! *prefix-filter* {4 "\"GET /__utm.gif"})

;; log format is loganis format: comma separators and 10 columns.
(config! *log-format* :urchin)

;; a load of input files. the program keeps the order
(config! *gackup-file*
	 ["/logs/access.1.gz"
	  "/logs/access.2.gz"
	  "/logs/access.3.gz"
	  "/logs/access.4.gz"
	  "/logs/access.5.gz"
	  "/logs/access.6.gz"
	  "/logs/access.7.gz"
	  "/logs/access.8.gz"
	  "/logs/access.9.gz"
	  "/logs/access.10.gz"
	  "/logs/access.11.gz"
	  "/logs/access.12.gz"])

;; mongo connection to use. 
(config! *mongo-conn*
         "Default MongoDB database info to connect."
         "This will be used when no command line parameter is given."
         {:host "localhost"  ; host name
          :port 27017        ; port number
          :user nil          ; user name. may be nil.
          :pwd nil           ; password for user
          :db  :gackup       ; database name in mongo.
          })

