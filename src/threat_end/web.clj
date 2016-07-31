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
           [:div {:class "header"} "Threat End"]
           logo
           [:div
            [:a {:class "prettyLink" :href "/login"}
             "Login"]
            ]
           [:div
            [:a {:class "prettyLink" :href "/register"}
             "Register"]
            ]
           ]
          ]
         ]
        )
  )

(defn login
  []
  (html [:html
         [:head css]
         [:body
          [:div {:class "container"}
           [:div {:class "header"} "Login"]
           logo
           [:form {:action "/forage" :method "get"}
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
           [:div {:class "header"} "Login"]
           logo
           [:form {:action "/forage" :method "get"}
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
          [:div {:class "container"}
           [:div {:class "header"} "Forage"]
           [:div {:class "news"} "Animal News"]
           [:div {:class "challenges"} "You have a new challenge"]
           [:div {:class "suggestedSpecies"} "Suggested Species"]
           
           ]
          ]
         ]
        )
  )
