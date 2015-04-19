(ns reagent_demo.views.home_page
  (:require [reagent.core :as reagent :refer [atom]]))


(defn rand-hex-char []
  (char (rand-nth (concat (range 48 58) (range 66 72)))))

(defn rand-hex []
  (str "#" (rand-hex-char) (rand-hex-char) (rand-hex-char)))

(defn swap-color! []
  (println "yo")
  (reset! color (rand-hex)))

(defn color-updater! [color]
  (js/setInterval #(swap-color! color) 500))

(defn main []
  (let [color (atom "red")]
    (fn []
      (js/setTimeout #(reset! color (rand-hex)) 700)
       [:div [:h2 {:style {:color @color} :on-click #(js/alert "asdf")} "Welcome to reagent_demo"]
        [:div [:a {:href "#/about"} "go to about page"]]])))


; (defn main []
;   (let [seconds (atom 0)
;         click-count (atom 0)]
;     (fn []
;       (js/setTimeout #(swap! seconds inc) 1000)
;       [:div
;        "Seconds Elapsed: " @seconds
;        [:div {:on-click #(swap! click-count inc)}
;         "I have been clicked " @click-count " times."]])))
