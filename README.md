gackup
======

Gentle Analytics Backup - using MongoDB, Clojure and Incanter


What is Gackup?
---------------

Gackup is a log file processing command line utility written in Clojure and using MongoDB.

If you use Urchin log format you can import log data details into MongoDB.


Installation
------------

> git clone https://github.com/loganisarn/gackup.git
>
> cd gackup
>
> lein uberjar

Usage
-----

> java -jar target/gackup-0.1.3-standalone.jar
>
> Gackup - Gentle Analytics Backup - log processing utility
> Usage:
> 
>  -f, --file                            Input log file                          
>  -d, --directory                       Input log directory                     
>  -Q, --quick-filter                    Quick prefix filter for column string   
>  -R, --detail-filter                   Regex filter for column string          
>  -F, --prefix-filter                   Filter map for computed values          
>  -O, --out                             Output type                             
>  -g, --logformat                       Log file format                         
>  -s, --chunk-size                      Number of lines in a chunk for parallel 
>
>  -c, --config                          Configuration file location             
>  -v, --no-verbose, --verbose           Turn on verbose mode                    
>  -h, --no-help, --help        false    Displays help information   

See examples subdirectory: https://github.com/loganisarn/gackup/tree/master/examples



