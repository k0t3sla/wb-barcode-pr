(ns wb.getwb
  (:require
   [muuntaja.core :as m]
   [clj-http.lite.client :as client]
   [wb.db.core :as db]
   [wb.config :refer [env]])
  (:import java.util.Base64))

(defn base64-decode [to-decode]
  (String. (.decode (Base64/getDecoder) to-decode)))

(defn convert-svg [m]
  (let [svg (base64-decode (:wbsticker m))]
    (-> (dissoc m :wbsticker) 
        (assoc :wbsticker svg)
        #_(assoc :timestamp (java.util.Date.)))))

(defn get-wb-data! [order-id]
  (let [resp (client/get "https://mp.e-teleport.ru/YandexBeru/hs/marketsBot/getWBStickers"
                         {:body (->> {:orders [(str order-id)]
                                      :type "json"
                                      :sort "article_imt"
                                      :wbstickers true}
                                     (m/encode "application/json"))
                          :headers {"Authorization", (env :token)}
                          :content-type :json
                          :socket-timeout 1000
                          :conn-timeout 1000
                          :accept :json})
        data (get-in (->> (:body resp)
                          (m/decode "application/json")) [0])]
    (when (not= nil data)
      (convert-svg data))))

(comment
  (get-wb-data! "162142455")
  (db/create-order! (get-wb-data! "162142455"))
  (clojure.pprint/pprint (db/get-order (:id 1)))
  )

