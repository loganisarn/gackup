;; GACKUP example config file
;; Complex filter example
;; Find visits with referer string containing "Online" and user agent Firefox or MSIE

(config! *quick-filter* {4 #"Online"})
(config! *detail-filter* {:agent #"Firefox|MSIE"})

(config! *log-format* :urchin)
(config! *gackup-file*
	["/logs/access.1.gz"])

