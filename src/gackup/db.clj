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


;; Index functions

(defn mongo-indexfor!
  "      params
   coll: insert to this collection
     kv: document to insert
   ckey: name for counter
      m: group by value
      i: number of tries, maximum depth of recursion"
  ([coll, kv]          (mongo-indexfor! coll kv :idx))
  ([coll, kv, ckey]    (mongo-indexfor! coll kv ckey {}))
  ([coll, kv, ckey, m] (mongo-indexfor! coll kv ckey m 6))
  ([coll, kv, ckey, m, i]
     (let [ct-coll :x-counters
           ct-indx :x-indexers
           row     (merge m kv)
           waitfn  (fn [x] (+ 5 (* 5 x)))
           found   (-> (fetch-one coll :where row) first ckey)]
       (cond
        found     found
        (neg? i)  (throw (Exception. (str "(mongo-indexfor! " coll kv ckey m i ")")))
        :else     (let[ct (-> (fetch-and-modify
                               ct-coll
                               row
                               {:$inc {:seq 1}}
                               :return-new? true
                               :upsert? true)
                              :seq int)]
                    (if (= ct 1)
                      (let[ind (-> (fetch-and-modify
                                    ct-indx
                                    {:_id (assoc m :_coll coll)}
                                    {:$inc {:seq 1}}
                                    :return-new? true
                                    :upsert? true)
                                   :seq int)]
                        (insert! coll (assoc row ckey ind))
                        (destroy! ct-coll row)
                        ind)
                      (do
                        (Thread/sleep (waitfn ct))
                        (recur coll kv ckey m (dec i)))
                      ))))))


(defn mongo-keyind! [k] (mongo-indexfor! :logdatakeys {:s k} :i {}))
(defn mongo-valind! [k sv] (mongo-indexfor! :logdict {:sv sv} :iv {:k k}))

(def m-mongo-keyind! (memoize mongo-keyind!))
(def m-mongo-valind! (memoize mongo-valind!))

(defn mongo-index-map-keys!
  [m] (apply merge {} (map (fn [[k v]] {(m-mongo-keyind! k) v}) m)))

(defn mongo-index-map-vals!
  [m] (apply merge {} (map
                       (fn [[k v]] {k
                                    (cond (number? v) v
                                          (nil? v)    nil
                                          :else       (m-mongo-valind! k v))}) m)))



(defn insert-chunk!
  "Inserts chunk to mongodb server. Returns chunk untouched."
  [chunk]
  (do
    (dorun (map (fn [line] (insert! log-data-name line)) chunk ))
    chunk))

