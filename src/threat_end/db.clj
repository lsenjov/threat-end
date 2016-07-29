(ns threat-end.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [taoensso.timbre :as log]
            )
  )

(def conn (mg/connect))
(def database (mg/get-db db "test"))

(defn upsert-kingdom
  "Finds if a kingdom exists, and inserts if it doesn't"
  [kMap]
  nil ;;TODO
  )
(map dissoc (mc/find-maps c "testa" {}) (repeat :_id))
(mc/insert c "testa" {:a 5 :b 10})


