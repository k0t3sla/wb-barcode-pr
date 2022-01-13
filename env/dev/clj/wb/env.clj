(ns wb.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [wb.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[wb started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[wb has shut down successfully]=-"))
   :middleware wrap-dev})
