(ns threat-end.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.conversion :as mcon]
            [taoensso.timbre :as log]
            [clojure.data.json :as json]
            )
  )

(def speciesColl "species")
(def locationColl "location")

(def conn (mg/connect))
(def database (mg/get-db conn "threatend"))

(log/set-level! :trace)

(defn find-species
  "Finds a single species map, or returns nil if not found.
  Can take a string or map, will search for \"ScientificName\" or :ScientificName"
  [species]
  (log/trace "find-species." species)
  (if (string? species)
    (dissoc (first (mc/find-maps database speciesColl {:ScientificName (clojure.string/capitalize species)})) :_id)
    (if-let [s (:ScientificName species)]
      (recur s)
      (if-let [s1 (get species "ScientificName")]
        (recur s1)
        (do
          (log/error "Could not find species from:" species)
          nil
          )
        )
      )
    )
  )

(defn find-geo
  "Finds a geolocation object, returns nil if not found. Takes a map"
  [gMap]
  (log/trace "find-geo." gMap)
  (first (mc/find-one-as-map database locationColl gMap))
  )

(defn upsert-species
  "Finds if a species exists. If it doesn't, add it. Returns the map"
  [{sName :ScientificName :as sMap}]
  (log/trace "Upserting species:" sName)
  (if (find-species sMap)
    (log/debug "Species:" sName "already in database, skipping")
    (mc/insert database speciesColl sMap)
    )
  sMap
  )

(defn refresh-data
  "Scrapes and uploads data directly to the database. Will take a while."
  []
  (-> "http://environment.ehp.qld.gov.au/species/?op=getkingdomnames"
      slurp
      json/read-str
      (get "Kingdom")
      ((partial mapcat (comp 
                         #(get % "Class")
                         json/read-str
                         slurp
                         #(get % "ClassNamesUrl"))))
      ;(#(list (first %)))
      ((partial mapcat (comp #(get % "Family") json/read-str slurp #(get % "FamilyNamesUrl"))))
      ;(#(list (first %)))
      ((partial mapcat (comp #(get % "Species") json/read-str slurp #(get % "SpeciesUrl"))))
      ;(#(list (first %)))
      ((partial map upsert-species))
      )
  )

(defn upsert-geolocation-with-user
  "Inserts a sighting to the database from a user. Returns a status map"
  [^String uName ^String species ^Double x ^Double y]
  (log/trace "Adding geolocation for species:" species "details:" uName x y)
  (if (find-species species)
    ;; Species exists
    (if (find-geo {:x x :y y :species species :foundBy uName})
      (json/write-str {:status "error" :message "Already added to database"})
      ;; Species exists, hasn't been added to database
      (do
        (log/trace "Inserting location to database")
        (mc/insert database locationColl {:x x :y y :species species :foundBy uName})
        (json/write-str {:status "okay" :message "Added sighting to database"})
        )
      )
    (json/write-str {:status "error" :message "Invalid species"})
    )
  )
    

(defn- upsert-geolocation
  [{{coords "coordinates"} "geometry" :as gMap}]
  (log/trace "Coords is:" coords)
  (let [newG (-> gMap
                 (assoc :x (first coords))
                 (assoc :y (second coords))
                 (assoc :species (-> gMap
                                     (get "properties")
                                     (get "ScientificName")
                                     )
                        )
                 )
        ]
    (if (find-geo {:x (:x newG) :y (:y newG) :species (:species newG)})
      (do
        (log/trace "Found geolocation, skipping")
        nil
        )
      (do
        (log/trace "Logging geolocation:" newG)
        (mc/insert database locationColl newG)
        nil
        )
      )
    )
  )

(defn- upsert-geolocation-from-url
  "Takes a species url, grabs geolocation data from the external database and adds it locally"
  [url]
  (-> url
      slurp
      json/read-str
      (get "SpeciesSightingsUrl")
      slurp
      json/read-str
      (get "features")
      ((partial map upsert-geolocation))
      doall
      )
  )

(defn refresh-geolocation
  "Scrapes all species in the database and add any database geolocation to the local database from the external database"
  []
  (->> (mc/find-maps database speciesColl {} {:SpeciesProfileUrl 1})
       (pmap (comp upsert-geolocation-from-url :SpeciesProfileUrl))
       )
  )

;; If this hasn't been completely loaded yet, means it isthe first time it's been loaded this session.
;; Begin scraping data into the database

(def started-scrape false)

(defn start-scrape
  []
  (if started-scrape
    "Already started"
    (do (future (doall (refresh-data))
                (doall (refresh-geolocation)))
        (def started-scrape true)
        (log/info "Scraping has begun!")
        "Beginning Scraping"
        )
    )
  )

