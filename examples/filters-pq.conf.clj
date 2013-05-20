;; using prefix and quick filtering for text "Online" in 404d pages with firefox browser


(config! *prefix-filter* {5 "404"})
(config! *quick-filter* {4 #"Online" 8 #"Firefox"})

(config! *log-format*  :urchin)
(config! *gackup-file* ["/logs/access.2.gz"])
