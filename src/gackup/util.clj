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


;; Utility functions
(in-ns 'gackup.core)

(defn collect-col-names
  "Returns a set of keys for a list of maps."
  [data]
  (set (apply concat (map keys data))))


(def b64-alphabet "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_")
(def b64-range (range 0 64))
(def b64-invalph (zipmap b64-alphabet b64-range))

(defn int-to-b64str
  "Turns positive integer to base 64 string representation"
  [i]
  (cond
   (nil? i)   nil
   (zero? i)  "0"
   :else
   (loop [out "" j i]
     (if (zero? j) out (recur (str (get b64-alphabet (mod j 64)) out) (quot j 64))))))

(defn b64str-to-int
  "Turns base 64 representation of number to integer"
  [s]
  (loop [in s i 0]
    (if (empty? in) i
        (recur (rest in) (+ (* i 64) (b64-invalph (first in))))
        )))

;; Compressed Data Stream

(defn gz-reader
  "Return a buffered reader for a gzipped file"
  [file-name]
  (-> (FileInputStream. file-name)
      GZIPInputStream.
      InputStreamReader.
      BufferedReader.))

(comment ; gz-reader local test
  (line-seq (gz-reader "/home/jano/dev/gackup-log/access.2.kicsi.log.gz")))

(defn gz-writer
  "Return a buffered writer for a gzipped file"
  [file-name]
  (-> (FileOutputStream. file-name)
      GZIPOutputStream.
      OutputStreamWriter.
      BufferedWriter.))

;; Utility type conversions

(defn integer-pattern
  "Fetches integer string or nil. Removes positive sign."
  [str]
  (some identity (rest (re-matches #"^(\d+)|(-\d+)|\+(\d+)$" str))))

(defn string-to-int
  "String to integer conversion, returns nil for malformed string.
   Please note that this function also handles positive signs,
   while Java 6 Integer/parseInt does not."
  [str]
  (if (nil? str) nil
      (if-let [ n (integer-pattern str)]
        (Integer/parseInt n) nil)))

(defn string-to-long
  "String to long integer conversion, returns nil for malformed string.
   Please note that this function also handles positive signs,
   while Java 6 Long/parseLong does not."
  [str]
  (if (nil? str) nil
      (if-let [ n (integer-pattern str)]
        (Long/parseLong n) nil)))

(defn string-to-num
  ""
  [s]
  (if-let [l (string-to-long s)]
    (if (> l 2147483647) l (int l))))

(defn ip-to-int
  "IP address string to integer conversion. Returns 0 on illegal input."
  [str]
  (let [parts (clojure.string/split str  #"\.")
        partsc (count parts)]
    (if (= partsc 4)
      (int (+ (* (Integer/parseInt (nth parts 0)) (* 256 256 256))
              (* (Integer/parseInt (nth parts 1)) (* 256 256))
              (* (Integer/parseInt (nth parts 2)) 256)
              (Integer/parseInt (nth parts 3))
              (- (* 128 256 256 256))))
      (int 0))))

(defn date-to-int
  "Date string to integer conversion"
  [s]
  (int (/ (-> (clj-time.format/formatter apache-log-date-format (clj-time.core/default-time-zone))
              (clj-time.format/parse s)
              (clj-time.coerce/to-long)) 1000)))

(defn flatten-map [m]
  "Flatten maps recursively."
  (let [process (fn [[k v]]
                  (if (map? v)
                    (apply merge(map (fn [[kk vv]]  {(keyword (str (name k) "-" (name kk))) vv}) v))
                    {k v} ))
        processed (apply merge (map process m))]
    (if (some map? (vals processed))
      (recur processed)
      processed)))

(defn map-remove-nils
  "Removes nil values from a map."
  [m]
  (apply hash-map (flatten (remove #(nil? (second %)) m))))

(defn trim-borders
  "Removes first and last characters of string. Useful for removing double quote chars."
  [s] (subs s 1 (dec (count s))))

(defn escape-regex-char
  "Returns escaped string for a regex character"
  [c]
  (if (contains? #{ \\ \^ \$ \( \) \{ \} \[ \] \. \* \+ \? \| \< \> \- \& \% } c)
    (str \\ c) c))

(defn file-to-lines
  "Opens file and returns a lazy sequence of lines. Decompresses gzip on .gz file"
  [fname]
  (cond
   ;; gzip
   (.endsWith fname ".gz") (->> fname gz-reader line-seq)
   (.endsWith fname ".zip") (throw (Exception. "zip file format not impl yet."));(->> fname zip-reader line-seq)
   (.endsWith fname ".bz")  (throw (Exception. ".bz file format not impl yet."));(->> fname bz-reader line-seq)
   (.endsWith fname ".lz4") (throw (Exception. ".lz4 file format not impl yet."));(->> fname lz4-reader line-seq)
   ;; uncompressed
   :else (->> fname clojure.java.io/reader line-seq)
   ))

(defn get-env
  "Get value of environment variable or returns default (nil)"
  ([env] (System/getenv env))
  ([env val] (or (System/getenv env) val)))

(defn remquote [^String s]
  "Removes trailing and ending double quote symbols."
  (if (and (.startsWith s "\"") (.endsWith s "\""))
    (.substring s 1 (dec (.length s)))
    s))


(defn decode-urlstr
  [text]
  (if (empty? text) nil
      (let 
          [un1 (clojure.string/replace text #"%u([0-9a-fA-F]{4})" (fn [[_ i]] (str (char (Integer/parseInt i 16)))))
           un2 (try (java.net.URLDecoder/decode un1 "UTF-8")
                    (catch Exception e un1))]
        un2))) 

(defn map-matches-filter?
  "Decides all values in a map of filters match the corresponding values in an other map."
  [mfilter line]
  (every? (fn [[k p]] (if-let [v (line k)] (re-find p (str v)))) mfilter))

(defn map-b64-keys
  "B64 encodes all integer keys in map"
  [m]
  (apply merge {} (map (fn [[k v]] {(int-to-b64str k) v} ) m)))

(defn map-with-try
  "Executes f on each item of l. Prints line and stack trace for each exception."
  [f l]
  (map
   #(try (f %)
         (catch Exception e
           (do (println "Exception on " f " for " %)
               (.printStackTrace ^Exception e (java.io.PrintWriter. *out*)) nil))) l))

(defn decompose-params
  "Decompose URL params into map"
  [s]
  (if-not (or (empty? s) (nil? s))
    (apply merge
           (map (fn [[k v]] {(keyword k) v} )
                (map (fn [l]  (clojure.string/split l #"=" 2))
                     (clojure.string/split s #"\&"))))
    {}))

(defn decompose-url
  "Convert url string to map"
  [referer]
  (let [ [[_ ref-url ref-par]] (re-seq #"([^?]*)(?:\?(.*))?" referer) ]
    {:url (if (= ref-url "-") nil ref-url)
     :params (decompose-params ref-par)}))

(defn decompose-url-urlparam
  "decompose url function with 'url' parameter decomposed with the same function"
  [s]
  (let [l (decompose-url (decode-urlstr s))]
    (if (empty? (-> l :params :url))
      l  
      (assoc-in l [:params :url] (decompose-url-urlparam (-> l :params :url))))))

(defn decompose-referer
  "Decomposese request string"
  [s]
  (decompose-url s))

(defn decompose-request
  "Decompose request string to map"
  [reqstr]
  (let [[[_ met base params protocol]]  (re-seq #"^(.*) ([^?]*)(?:\?(.*))? (.*)$" reqstr)
        obj {:method met :url base :protocol protocol}]
    (cond
     (= base utm-gif) (merge obj {:utmgif (decompose-params (decode-urlstr params))})
     :default (merge obj {:params params}))))

(defn decompose-cookie
  "Process cookie string to map"
  [cookiestr]
  (cond
   (= cookiestr "-") nil
   (= cookiestr "")  nil
   :else
   (let
       [cookie cookiestr
        utma-keys [:domain-hash :visitor-id :visit-first :visit-last :visit-current :visit-count]
        utmb-keys [:domain-hash :session-page-view-count :session-event-count :timestamp-visit-current]
        utmz-keys [:domain-hash :cookie-timestamp :session-number :campaign-number]
        ckv    (apply assoc {} (flatten (map #(clojure.string/split % #"=" 2)
                                             (clojure.string/split cookie #" *; *"))))
        utma-map  (if-let [utma (ckv "__utma")]
                    (zipmap utma-keys
                            (map (fn [x] (string-to-num x))
                                 (clojure.string/split utma #"\." ))))
        utmb-map  (if-let [utmb (ckv "__utmb")]
                    (zipmap utmb-keys (map (fn [x] (string-to-num x))
                                           (clojure.string/split utmb #"\." ))))
        utmc      (string-to-num (ckv "__utmc" "-"))
        utmz-ary  (if-let [utmz (ckv "__utmz")]
                    (if-not (empty? utmz) (clojure.string/split utmz #"\." 5)))
        utmz-head (if utmz-ary (apply merge (map-indexed
                                             (fn [idx itm] {itm (string-to-num (get utmz-ary idx))})
                                             utmz-keys)))
        utmz-tail-raw (if utmz-ary (apply hash-map (flatten (map #(clojure.string/split % #"=")
                                                                 (clojure.string/split (get utmz-ary 4 "") #"\|")))))
        utmz-tail (if utmz-tail-raw
                    {:campaign-source  (utmz-tail-raw "utmcsr")
                     :campaign-name    (utmz-tail-raw "utmccn")
                     :last-keyword     (decode-urlstr (utmz-tail-raw "utmctr"))
                     :campaign-medium  (utmz-tail-raw "utmcmd")
                     :campaign-id      (utmz-tail-raw "utmcid")
                     :campaign-content (decode-urlstr (utmz-tail-raw "utmcct"))
                     :unique-id        (utmz-tail-raw "utmgclid")})
        utmz      (merge utmz-head utmz-tail)
        utmv-ary  (clojure.string/split (ckv "__utmv" ".") #"\." 2)
        utmv      {:domain-hash (string-to-num (first utmv-ary)) :value (second utmv-ary)}
        utmx      (ckv "__utmx" nil)
        res  {:utma utma-map
              :utmb utmb-map
              :utmc utmc
              :utmz utmz
              :utmv utmv
              :utmx utmx}
        others (dissoc ckv "__utma" "__utmb" "__utmc" "__utmz" "__utmv" "__utmx")]
     (flatten-map (assoc res :other others))
     )))



(defmacro config!
  "Changes root binding for val. Return new value."
  ([n & v] `(alter-var-root (var ~n) (constantly ~(last v)))))


(defn split-line
  "Super fast split line using given sperator. DOES handle quotes."
  ([#^"String" line] (split-line \, line))
  ([sep, #^"String" line]
     (loop [cursor (int 0)
            nq (.indexOf line (int \"))
            nc (.indexOf line (int sep))
            sections []]
       (cond
        (= -1 nc)
        (conj sections (.substring line cursor))
        (or (= -1 nq) (> nq nc))
        (recur (inc nc)
               nq
               (.indexOf line (int sep) (inc nc))
               (conj sections (.substring line cursor nc)))
        :else
        (let [qe (.indexOf line (int \") (inc nq))]
          (if (= qe -1)
            (throw (Exception. (str "unmatched double quote symbols in: " line)))
            (recur cursor
                   (.indexOf line (int \")  (inc qe))
                   (.indexOf line (int sep) (inc qe))
                   sections)))))))

(defn third 
  "Returns third item in collection."
  [x] (-> x next next first))

