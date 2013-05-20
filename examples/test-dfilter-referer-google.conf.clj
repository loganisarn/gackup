;; GACKUP example config file
;; looking for visits referred from google.com


(config! *detail-filter* {:referer-url #"google\.com"})

(config! *log-format* :urchin)
(config! *gackup-file*
	 ["/logs/access.2.gz"])
