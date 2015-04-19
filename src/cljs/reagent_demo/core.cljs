(ns reagent-demo.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react])
    (:import goog.History))

;; -------------------------
;; Views

(def color (atom "red"))

(defn home-page []
    [:div [:h2 {:style {:color @color}} "Welcome to reagent_demo"]
     [:div [:a {:href "#/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About "]
   [:div [:a {:href "#/"} "go to the home page"]]
   [:div [:a {:href "#/clock"} "go to the clock page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

(defonce timer (atom (js/Date.)))

(defn rand-hex-char []
  (char (rand-nth (concat (range 48 58) (range 66 72)))))

(defn rand-hex []
  (str "#" (rand-hex-char) (rand-hex-char) (rand-hex-char)))

(defn interval []
  (reset! color (rand-hex))
  (reset! timer (js/Date.)))

(defonce time-updater (js/setInterval interval 500))

(defn clock []
  (let [time-str (-> @timer .toTimeString (clojure.string/split " ") first)]
    [:div.example-clock
     time-str]))

(defn clock-page []
  [:div [:h2 "About reagent_demo"]
   [:div [:a {:href "#/"} "go to the home page"]]
   [:div [clock]]])

(defn ^:export run []
  (reagent/render [clock-page]
                  (js/document.getElementById "app")))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

(secretary/defroute "/clock" []
  (session/put! :current-page #'clock-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
