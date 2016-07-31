(ns threat-end.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.util.response :as resp]
            [taoensso.timbre :as log]
            [threat-end.api :as api]
            [threat-end.db :as db]
            [clojure.data.json :as json]
            [threat-end.web :as web]
            ))

(log/set-level! :trace)

(defroutes app-routes
  (GET "/api/new-user/:username/:password/:email/:region/"
       {{uName :username pass :password email :email region :region} :params}
       (json/write-str (api/create-user uName pass email region)))
  (GET "/api/login/:username/:password/"
       {{uName :username pass :password} :params}
       (json/write-str (api/user-login uName pass)))
  (GET "/api/useraccount/:session/"
       {{session :session} :params}
       (json/write-str (api/get-user session)))
  (GET "/api/addfriend/:session/:friend/"
       {{session :session friend :friend} :params}
       (json/write-str (api/add-friend session friend)))
  (GET "/api/species/scientific/:speciesName/"
       {{speciesName :speciesName} :params}
       (json/write-str (api/find-species-by-scientific-name speciesName)))
  (GET "/api/species/atlas/:speciesName/"
       {{speciesName :speciesName} :params}
       (api/get-living-atlas-by-species speciesName))
  (GET "/api/species/nearby/:xPos/:yPos/:radiusInMetres/"
       {{xPos :xPos yPos :yPos radius :radiusInMetres} :params}
       (json/write-str (api/get-all-nearby xPos yPos radius)))
  (GET "/api/addsighting/:session/:species/:xPos/:yPos/"
       {{xPos :xPos yPos :yPos session :session species :species} :params}
       (api/add-sighting session species xPos yPos))
  (GET "/api/startscrape/" [] (db/start-scrape))

  ;; Demo stuff
  (GET "/index" [] (web/index))
  (GET "/login" [] (web/login))
  (GET "/forage" [] (web/forage))

  (GET "/" [] (resp/redirect "/index"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))
