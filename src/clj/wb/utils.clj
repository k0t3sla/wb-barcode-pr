(ns wb.utils
  (:require [clojure.string :as string]))

(defn not-empty? [strings]
  (filter (complement string/blank?) strings))

(defn valid-orders? [input-str]
  (every? #(= (count %) 9)
          (not-empty? (string/split input-str #"\D"))))





(comment

  )


#_(db/delete-old-orders!)



