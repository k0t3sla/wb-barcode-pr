(ns wb.routes.home
  (:require
   [wb.layout :as layout]
   [wb.db.core :as db]
   #_[clojure.java.io :as io]
   [wb.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [struct.core :as st]
   [clojure.string :as string]
   [wb.getwb :as getwb]
   [wb.utils :as utils]))

(def last-insert (atom 0))
(def last-error (atom ()))

(def order-schema
  [[:order_id
    st/required
    st/string
    {:message "неправильно введен номер заказа"
     :validate utils/valid-orders?}]])

(defn validate-order [params]
  (first (st/validate params order-schema)))

(defn save-order! [{:keys [params]}]
  (if-let [errors (validate-order params)]
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (reset! last-insert 0)
      (reset! last-error [])
      (let [orders (clojure.string/split (:order_id params) #"\D")]
        (doseq [order orders]
          (if-let [order-data (getwb/get-wb-data! order)]
            (do (db/create-order! order-data)
                (swap! last-insert inc))
            (swap! last-error conj order))))
      (response/found "/"))))


(defn home-page [{:keys [flash] :as request}]
  (layout/render
   request
   "home.html"
   (merge {:last-edded @last-insert
           :last-error @last-error
           :orders (db/get-today-orders)}
          (select-keys flash [:orders :errors]))))

(defn yesterday-page [{:keys [flash] :as request}]
  (layout/render
   request
   "yesterday.html"
   (merge {:last-edded @last-insert
           :last-error @last-error
           :orders (db/get-yesterday-orders)}
          (select-keys flash [:orders :errors]))))

(defn all-orders [{:keys [flash] :as request}]
  (layout/render
   request
   "all-orders.html"
   (merge {:last-edded @last-insert
           :last-error @last-error
           :orders (db/get-orders)}
          (select-keys flash [:orders :errors]))))

(defn order-page [{:keys [flash] :as request}]
  (layout/render
   request
   "order.html"
   (let [order (db/get-order {:order_id (:id (:path-params request))})]
     (merge {:test (str (:id (:path-params request)))
             :order (db/get-order order)}
            (select-keys flash [:order :errors])))))


(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page
         :post save-order!}]
   ["/order-{id}" {:get order-page}]
   ["/yesterday" {:get yesterday-page
             :post save-order!}]
   ["/all-orders" {:get all-orders
                   :post save-order!}]])



(comment
  
  (def request {:path-params {:id "162142455"}})

  (db/get-order {:order_id (:id (:path-params request))})


  )