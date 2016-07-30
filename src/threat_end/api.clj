(ns threat-end.api
  (:require [taoensso.timbre :as log]
            [threat-end.db :as db]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.conversion :as mcon]
            )
  )

(def ^:private userColl "users")

(defn- uuid
  "Creates a unique identifier"
  []
  (str (java.util.UUID/randomUUID))
  )

(defn create-user
  "Creates and adds the user to the database"
  [^String uName ^String pass]
  (log/trace "create-user:" uName)
  (if (or (= 0 (count uName)) (= 0 (count pass)))
    {:status "error" :message "Length cannot be 0"}
    (if (mc/find-one db/database userColl {:username uName})
      {:status "error" :message "Username already exists"}
      (let [u (uuid)]
        (mc/insert db/database userColl {:username uName :pass (hash pass) :session u})
        {:status "okay" :session u}
        )
      )
    )
  )

(defn user-login
  "Gets the user session with a username and password"
  [^String uName ^String pass]
  (log/trace "user-login:" uName)
  (if (mc/find-one-as-map db/database userColl {:username uName})
    (let [u (uuid)]
      (mc/update db/database userColl {:username uName} {"$set" {:session u}})
      {:status "okay" :session u}
      )
    )
  )

(create-user "Testname" "1234")
(user-login "Testname" "1234")

(def ses (:session (user-login "Testname" "1234")))

(defn get-user
  "Gets the user object by session"
  [^String session]
  (if-let [u (mc/find-one-as-map db/database userColl {:session session})]
    {:status "okay" :user (dissoc u :pass)}
    {:status "error" :message "Invalid session"}
    )
  )

(:user (get-user ses))
