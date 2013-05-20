;;   Copyright 2013 iWebMa Ltd.

;;   Licensed under the Apache License, Version 2.0 (the "License");
;;   you may not use this file except in compliance with the License.
;;   You may obtain a copy of the License at

;;       http://www.apache.org/licenses/LICENSE-2.0

;;   Unless required by applicable law or agreed to in writing, software
;;   distributed under the License is distributed on an "AS IS" BASIS,
;;   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;;   See the License for the specific language governing permissions and
;;   limitations under the License.

(in-ns 'gackup.core)

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(def postprocess-line
  "Performs basic filtering on log entry object: nil checks, index mapping, etc."
  (let [filter-nils* (if *insert-if-empty* identity map-remove-nils)]    
    (fn [m] (-> m filter-nils* 
                mongo-index-map-keys! mongo-index-map-vals!
                map-b64-keys))))

(defn action-mongo
  "This is the :mongo action. Does key and val indexing and mongo insert."
  [[l c m]]
  (->> m postprocess-line (insert! log-data-name)))

(def  action-stdout-agent (agent nil))
(defn action-stdout-print [s] (send action-stdout-agent (fn [_] (println s))))
(defn action-stdout [[l c m]] (action-stdout-print l))

(def gackup-actions {:mongo  action-mongo  
                     :stdout action-stdout})

(defn process-log-lines
  "default line processing fun"
  [lines]
  (let[
       sep (log-format-separators *log-format*)
       col-split     (fn [l] [l (split-line sep l) nil])
       filter-prefix (fn [[_ cols]] (every? (fn [[k v]]  (.startsWith (str (cols k)) v)) *prefix-filter*))
       filter-quick  (fn [[_ cols]] (map-matches-filter? *quick-filter* cols))
       ord           (log-format-orders *log-format*)
       p            #(-> % ord flatten-map)
       pextra       #(apply merge (map (fn [[k v]] (if-let [f (decompose-extra-cols k)]
                                                     (flatten-map {k (f v) })
                                                     {k v})) % ))
       obj-split     (fn [[s cols]]      [s cols (-> cols p pextra)])
       filter-detail (fn [[_ _ dets]]    (map-matches-filter? *detail-filter* dets))
       map-ignored   (fn [[l cols dets]] [l cols (apply dissoc dets decompose-ignore-cols)])
       action        (gackup-actions *gackup-output*)] 
    (reduce +
            (pmap
             #(->>
               %
               (map    col-split)    ;; splitting lines to columns
               (filter filter-prefix);; prefix filtering
               (filter filter-quick) ;; quick filtering with regex
               (map    obj-split)    ;; splitting cols to maps.
               (filter filter-detail);; filter vals in map with regex
               (map    map-ignored)  ;; remove ignored keys from map
               (map    action)       ;; action: eg mongo insert/stdout, etc..
               (count)
               ) (partition-all *chunk-size* lines)))))


(defn gackup-program*
  "Main gackup logics is here. Expects a map"
  [ opts ]
  (let [
        verbose* (opts :verbose *verbose-mode*)
        webserv* (opts :web     false)
        lines    (-> opts :file file-to-lines)]
    (do
      (if verbose* (println "params: " opts))
      (let [info (merge *mongo-conn* (opts :mongo))
            conn (make-connection (:db info) :host (:host info) :port (:port info))]
        (if (:user info)
          (if-not (authenticate conn (:user info) (:pwd info))
            (throw (Exception. (str "!Could not authenticate to mongo: " info)))))
        (set-connection! conn)
        (if verbose* (println "Connected to mongo.")))
      
      (add-index! :logdatakeys [[:i 1]] :unique true)
      (add-index! :logdatakeys [[:s 1]] :unique true)

      (add-index! :logdict [[:k 1] [:iv 1]] :unique true)      
      (add-index! :logdict [[:k 1] [:sv 1]] :unique true)
      (add-index! :logdict [[:k 1]])

      (binding[*prefix-filter*        (or (opts :pfilter) *prefix-filter*)
               *quick-filter*         (or (opts :qfilter) *quick-filter*)
               *detail-filter*        (or (opts :dfilter) *detail-filter*)
               *log-format*           (or (opts :format) *log-format*)
               *chunk-size*           (or (opts :csize) *chunk-size*)
               *verbose-mode*         (opts :verbose *verbose-mode*)
               *gackup-output*       (opts :output *gackup-output*)]

        (let [pinfo (process-log-lines lines)]
          (if verbose* (println "* lines processed: " pinfo ))))
      (if verbose* (println "* finished")))))

(defn gackup-program
  [& opts]
  (cond
   (even? (count opts))
   (gackup-program (apply hash-map opts))
   (and (= 1 (count opts)) (map? (first opts)))
   (let [ops (first opts)]
     (cond (-> ops :file coll?)
           (dorun (map 
                   #(gackup-program* (assoc ops :file %))
                   (:file ops)))
           :else
           (gackup-program* ops)))))


