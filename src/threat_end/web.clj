(ns threat-end.web
  (:require [taoensso.timbre :as log]
            [threat-end.db :as db]
            [clojure.data.json :as json]
            [hiccup.core :refer [html]:as h]
            )
  )

(def css [:link {:rel "stylesheet" :type "text/css" :href "css/style.css"}])

(defn index
  []
  (html [:html
         [:head
          css
          ]
         [:body
          [:div {:class "header"}
           "Threat End"
           ]
          [:div
           [:img {:src "img/logo.png" :class "logo"}]
           ]
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
        )
  )
