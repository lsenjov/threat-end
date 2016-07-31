(ns threat-end.web
  (:require [taoensso.timbre :as log]
            [threat-end.db :as db]
            [clojure.data.json :as json]
            [hiccup.core :refer [html]:as h]
            )
  )

(def css [:link {:rel "stylesheet" :type "text/css" :href "css/style.css"}])
(def logo [:div [:img {:src "img/logo.png" :class "logo"}]])

(defn index
  []
  (html [:html
         [:head css]
         [:body
          [:div {:class "container"}
           logo
           [:div {:class "container-sub"} [:h1 "Threat End"]]
           [:div {:class "form"}
            [:input {:type "text" :name "username" :placeholder "username"}]
            ]
           [:div {:class "form"}
            [:input {:type "text" :name "password" :placeholder "password"}]
            ]
           [:a {:class "prettyLink" :href "forage"}
            [:div {:class "but-go"} "Login"]
            ]
           [:a {:class "prettyLink" :href "forage"}
            [:div {:class "but-stop"} "Register"]
            ]
           ]
          ]
         ]
        )
  )

;; Not used for mockup
(defn login
  []
  (html [:html
         [:head css]
         [:body
          [:div {:class "container"}
           [:div {:class "container-sub"} "Login"]
           logo
           [:form {:action "forage" :method "get"}
            [:input {:type "text" :name "username"} "Username"]
            [:input {:type "text" :name "password"} "Password"]
            [:input {:type "submit" :value "Log In"}]
            ]
           ]
          ]
         ]
        )
  )
(defn register
  []
  (html [:html
         [:head css]
         [:body
          [:div {:class "container"}
           [:div {:class "container-sub"} "Login"]
           logo
           [:form {:action "forage" :method "get"}
            [:input {:type "text" :name "username"} "Username"]
            [:input {:type "text" :name "password"} "Password"]
            [:input {:type "text" :name "email"} "email"]
            [:input {:type "text" :name "region"} "region"]
            [:input {:type "submit" :value "Log In"}]
            ]
           ]
          ]
         ]
        )
  )

(defn forage
  []
  (html [:html
         [:head css]
         [:body
          [:div {:class "container-login"}
           [:div {:class "container"}
            [:div {:class "container-sub"} "Forage"]
            [:div {:class "container-sub"} "Animal News"]
            [:div {:class "container-sub"} "You have a new challenge"]
            [:div {:class "container-sub"} "Suggested Species"]
            [:a {:href "apilinks"}
             [:div {:class "container-sub"} "Api Demonstrations"]
             ]
            ]
           ]
          ]
         ]
        )
  )

(defn api-examples
  []
  (html [:html
         [:head css]
         [:body
          [:div {:class "container"}
           [:div {:class "container-sub"}
            [:a {:href "./api/species/nearby/152.7528/-27.6188/1000/"}
             "Show all nearby species sightings within 1000 metres of a coordinate" [:br]
             "/threatend/api/species/nearby/:xcoord/:ycoord/:distMetres/"
             ]
            ]
           [:div {:class "container-sub"}
            [:a {:href "./api/species/scientific/manorina melanocephala/"}
             "Get json data for a single species" [:br]
             "/threatend/api/species/scientific/:speciesName/"
             ]
            ]
           ]
          ]
         ]
        )
  )
