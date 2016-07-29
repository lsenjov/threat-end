(defproject threat-end "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 ;; Logging
                 [com.taoensso/timbre "4.5.1"]
                 ;; Json Deps
                 [org.clojure/data.json "0.2.6"]
                 ;; Displaying HTML
                 [hiccup "1.0.5"]
                 ;; MongoDB
                 [com.novemberain/monger "3.0.2"]
                 ;; Scraping http
                 [clj-http "2.2.0"]
                 ]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler threat-end.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
