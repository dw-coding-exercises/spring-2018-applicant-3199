(ns my-exercise.results
  (:require [hiccup.page :refer [html5]]
            [clojure.string :as st]
            [clojure.edn :as edn]
            [clj-http.client :as client]))

(def baseState "ocd-division/country:us/state:%s")
(def basePlace "ocd-division/country:us/state:%s/place:%s")
(def turboApi "https://api.turbovote.org/elections/upcoming?district-divisions=")

(defn header []
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "Your next elections"]
   [:link {:rel "stylesheet" :href "default.css"}]])

;; Print the elections to the screen
;; from the map fetched in getElections
(defn printElections [elections]
  [:div {:class "your-elections"}
      [:h1 "These are your upcoming elections:" ]
      (for [election elections]
        ;; Just name and website for now.
        [:a {:href (:website election)} 
          [:h2 (:description election)]])])

;; Generates the OCDs using city and state, 
;; and calls the turbovote API
(defn getElections [city state] 
  (let [OCDs 
    ;; for now we define this list here, but we can
    ;; have a function that generates this list according to what is passed
    (let [state-str (st/lower-case state) 
          place-str (st/replace (st/lower-case city) #" " "_")]
      (conj '() 
        (format basePlace state-str place-str)
        (format baseState state-str)))]
  ;; To avoid dealing with SSL we just do insecure connection.
  (edn/read-string (:body (client/get (str turboApi (st/join "," OCDs)) {:insecure? true})))))

;; Renders the page
(defn page 
  [{:keys [af street street2 city state zip]}]
  (html5
   (header)
   (printElections 
    ;; for now we only use city and state
    (getElections city state))))




