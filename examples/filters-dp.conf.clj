;; GACKUP example config file
;; Quick and detailed filtering example
;; task: find pages with status 404 or 500 for browser string: msie or opera or firefox


(config! *quick-filter* {5 #"500|404"})
(config! *detail-filter* {:agent #"MSIE|Opera|Firefox"})

(config! *log-format* :urchin)
(config! *gackup-file* ["/logs/access.1.gz"])

