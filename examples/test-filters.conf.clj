;; GACKUP example config file
;; Complex filter example
;; task: find visits in 20th february with referer string "Online" and user agent MSIE or iPhone


;; first round: prefix filtering
(config! *prefix-filter* {3 "[20/Feb"})

;; second round: the quick filter matches a regex to a column
(config! *quick-filter* {4 #"Online"})

;; third round: the detail filter matches a regex to a key
(config! *detail-filter* {:agent #"MSIE|iPhone"})

(config! *log-format* :urchin)
(config! *gackup-file* ["/logs/access.12.gz"])


