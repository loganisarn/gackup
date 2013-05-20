;; GACKUP example config file
;; Task: Look for visits with status code 404 or 500 using quick filtering.
;; Please note that regular expressions can be used to matching string alternatives.


;; Using loganis log format, column number 5 is the status code.
(config! *quick-filter* {5 #"404|500" })

(config! *log-format* :urchin)

(config! *gackup-file*
	 ["/logs/access.1.gz"])


