;; GACKUP example config file
;; Task: Use prefix filtering to find pages with status 404. 
;; Please note that string prefix matching is very fast compared to regex matching.


(config! *prefix-filter* {5 "404"})

(config! *log-format*  :urchin)

(config! *gackup-file* ["/logs/access.1.gz"])
