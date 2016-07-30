(ns threat-end.api
  (:require [taoensso.timbre :as log]
            [threat-end.db :as db]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.conversion :as mcon]
            [clojure.data.json :as json]
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
  [^String uName ^String pass ^String email ^String region]
  (log/trace "create-user:" uName)
  (if (or (= 0 (count uName)) (= 0 (count pass)))
    {:status "error" :message "Length cannot be 0"}
    (if (mc/find-one db/database userColl {:username uName})
      {:status "error" :message "Username already exists"}
      (let [u (uuid)]
        (mc/insert db/database userColl {:username uName :pass (hash pass) :session u :email email :region region})
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

(defn get-user
  "Gets the user object by session"
  [^String session]
  (log/trace "get-user." session)
  (if-let [u (mc/find-one-as-map db/database userColl {:session session})]
    {:status "okay" :user (-> u
                              (dissoc :pass)
                              (dissoc :_id)
                              )
     }
    {:status "error" :message "Invalid session"}
    )
  )

(defn- get-user-by-name
  "Gets user object by name. Returns nil if does not exist"
  [^String uName]
  (if-let [u (mc/find-one-as-map db/database userColl {:username uName})]
    (-> u (dissoc :pass) (dissoc :_id))
    nil
    )
  )

(defn- get-user-by-session
  "Gets user object by session. Returns nil if does not exist"
  [^String session]
  (if-let [u (mc/find-one-as-map db/database userColl {:session session})]
    (-> u (dissoc :pass) (dissoc :_id))
    nil
    )
  )

(defn- add-friend-one-way
  "Adds a friend to a user's list. Assumes the user exists"
  [^String uName ^String fName]
  (log/trace "Adding friend from" uName "to" fName)
  (let [{friends :friends :as u} (get-user-by-name uName)]
    (mc/update db/database userColl {:username uName} {"$set" {:friends (set (conj friends fName))}})
    )
  )

(defn add-friend
  "Adds another user as a friend"
  [^String session ^String fName]
  (let [{friends :friends uName :username :as u} (get-user-by-session session)]
    (log/trace "Add-friend. session:" session
               "\nfName" fName
               "\nusername" uName
               "\nfriends:" friends
               "\nuser:" u
               "\nsome:" (some (set fName) friends)
               "\nfirst:" (first friends))
    (if uName
      ;; For some reason this doesn't work! Must be a type difference
      (if (some (set fName) (map str friends))
        {:status "error" :message "Already a friend"}
        (do
          (add-friend-one-way uName fName)
          (add-friend-one-way fName uName)
          {:status "okay" :message (str "Added " fName " as a friend.")}
          )
        )
      {:status "error" :message "Invalid session"}
      )
    )
  )

(defn find-species-by-scientific-name
  "Finds the species referred to by the scientific name"
  [sName]
  (log/trace "find-species-by-scientific-name:" sName)
  (if-let [s (db/find-species sName)]
    {:status "okay" :species s}
    {:status "error" :message "Could not find species"}
    )
  )

(defn- metres-to-degrees
  "Changes a metres value to a degrees value"
  [^Double d]
  (float (/ d 111120)))

(defn- get-local-species
  "Gets all species names within a square of the designated location, with a bounding radius"
  [^Double x ^Double y ^Double r]
  (->> (mc/find-maps db/database db/locationColl
                     {
                      :x {"$gt" (- x r) "$lt" (+ x r)}
                      :y {"$gt" (- y r) "$lt" (+ y r)}
                      }
                     {
                      :species 1
                      :_id 0
                      }
                     )
       (map :species)
       set
       )
  )

(defn get-all-nearby
  "Gets all the nearby species names. Defaults to 3km radius"
  [^Double x ^Double y ^Double dist]
  (try {:status "okay"
        :message (get-local-species
                   (Double/parseDouble x)
                   (Double/parseDouble y)
                   (metres-to-degrees (Double/parseDouble dist))
                   )
        }
       (catch NumberFormatException e {:status "error" :message "Invalid number"})
       )
  )

(defn get-living-atlas-by-species
  "Gets the first result from the living atlas by species"
  [^String species]
  (if-let
    [r (-> (slurp (str "http://bie.ala.org.au/ws/search.json?q={" species "}"))
           json/read-str
           (get "searchResults")
           (get "results")
           (#(do (log/trace "ResultList:" %) %))
           first
           )
     ]
    (-> r
        (#(do (log/trace "Result:" %) %))
        (get "guid")
        (#(do (log/trace "Getting guid:" %) %))
        ((partial str "http://bie.ala.org.au/ws/species/"))
        slurp
        json/read-str
        (assoc :status "okay")
        json/write-str
        )
    (json/write-str {:status "error" :message "No results for search"})
    )
  )

(defn add-sighting
  "Adds a sighting of an animal"
  [^String session ^String species ^String x ^String y]
  (let [{uName :username} (get-user-by-session session)]
    (if uName
      (try (db/upsert-geolocation-with-user
             uName
             species
             (Double/parseDouble x)
             (Double/parseDouble y)
             )
           (catch NumberFormatException e {:status "error" :message "Invalid location"})
           )
      {:status "error" :message "Invalid session"}
      )
    )
  )



(get-living-atlas-by-species "Paracanthurus hepatus")


(get-local-species 152.9 -27.4 (metres-to-degrees 10000))
(get-all-nearby "152.9" "-27.4" "10000")
(map (comp :species find-species-by-scientific-name) (get-local-species 152.75 -27.61 (metres-to-degrees 10000)))
