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

(def decompose-extra-cols
  "Extra functions for each key"
  {:request-utmgif-params-search    decode-urlstr
   :request-utmgif-prev   decode-urlstr
   :request-utmgif-url   decompose-url-urlparam
   :request-utmgif-utmdt decode-urlstr
   :request-utmgif-utmfl decode-urlstr
   :request-utmgif-utmp decompose-url-urlparam
   :request-utmgif-utmp-params-cd string-to-int
   :request-utmgif-utmr-params-bih  #(or (string-to-int %) 0)
   :request-utmgif-utmr-params-dur string-to-int
   :request-utmgif-utmr-params-qsubts string-to-int
   :request-utmgif-utmr-params-sz string-to-int
   :request-utmgif-utmr-params-tbnh string-to-int
   :request-utmgif-utmr-params-tbnw string-to-int
   :request-utmgif-utmr-params-url-params-q decode-urlstr 
   :request-utmgif-utmr-url    decode-urlstr
   :request-utmgif-utmr decompose-url-urlparam
   })

(def decompose-ignore-cols
  "Column names to ignore"
  #{:referer-params-usg
    :request-utmgif-ei
    :request-utmgif-h
    :request-utmgif-q
    :request-utmgif-sig
    :request-utmgif-utmhid
    :request-utmgif-utmn
    :request-utmgif-utmp-params-ei
    :request-utmgif-utmp-params-usg
    :request-utmgif-utmr-params-CUI
    :request-utmgif-utmr-params-afdt
    :request-utmgif-utmr-params-barid
    :request-utmgif-utmr-params-ei
    :request-utmgif-utmr-params-gs_l
    :request-utmgif-utmr-params-h
    :request-utmgif-utmr-params-mid
    :request-utmgif-utmr-params-nhash
    :request-utmgif-utmr-params-q
    :request-utmgif-utmr-params-rlz
    :request-utmgif-utmr-params-sig2    
    :request-utmgif-utmr-params-tbnid
    :request-utmgif-utmr-params-u
    :request-utmgif-utmr-params-usg
    :request-utmgif-utmr-params-ved
    :request-utmgif-utmvp ; browser view port size (resolution WxH)    
    })

(defn log-format-order-urchin
  "Column vector to hash-map function. "
  [[ip domain username time request status size referer agent cookiestr & others]]
  {:ip       (ip-to-int ip)
   :domain   domain
   :username username
   :time     (date-to-int time)
   :request  (decompose-request (remquote request))
   :status   (string-to-num status)
   :size     (string-to-num size)
   :referer  (decompose-referer (remquote referer))
   :agent    (remquote agent)
   :cookie   (decompose-cookie (remquote cookiestr))
   :others   others
   })

(def log-format-orders
  {:loganis log-format-order-urchin
   :urchin  log-format-order-urchin})

(def log-format-separators
  {:loganis \,
   :urchin \space})
