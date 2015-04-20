(ns reagent_demo.views.home_page
  (:require [reagent.core :as reagent :refer [atom]]
            [hum.core :as hum]))

(defn rand-hex-char []
  (char (rand-nth (concat (range 48 58) (range 66 72)))))

(defn rand-hex []
  (str "#" (rand-hex-char) (rand-hex-char) (rand-hex-char)))

(defn swap-color! [color]
  (reset! color (rand-hex)))

(def ctx (hum/create-context))
(def vco (hum/create-osc ctx :square))
(def vcf (hum/create-biquad-filter ctx))
(def output (hum/create-gain ctx))

(hum/connect vco vcf)
(hum/connect vcf output)
(hum/start-osc vco)
(hum/connect-output output)

(def ctx2 (hum/create-context))
(def vco2 (hum/create-osc ctx :triangle))
(def vcf2 (hum/create-biquad-filter ctx))
(def output2 (hum/create-gain ctx))

(hum/connect vco2 vcf2)
(hum/connect vcf2 output2)
(hum/start-osc vco2)
(hum/connect-output output2)

(defn note-to-frequency [note]
  (let [expt-numerator (- note 49)
        expt-denominator 12
        expt (/ expt-numerator expt-denominator)
        multiplier (.pow js/Math 2 expt)
        a 440]
  (* multiplier a)))

(defn note-on [note] (hum/note-on output vco (note-to-frequency note)))
(defn note-on2 [note] (hum/note-on output2 vco2 (note-to-frequency note)))
(defn note-on3 [a b]
  (hum/note-on output vco (note-to-frequency a))
  (hum/note-on output2 vco2 (note-to-frequency b)))

(defn note-off [] (hum/note-off output))
(defn note-off2 [] (hum/note-off output2))
(defn note-off3 [] (note-off) (note-off2))

(defn note-button [note] [:button {:on-mouse-down #(note-on note) :on-mouse-up note-off} note])
(defn note-button2 [note] [:button {:on-mouse-down #(note-on2 note) :on-mouse-up note-off2} (str note " 2")])
(defn note-button3 [n1 n2] [:button {:on-mouse-down #(note-on3 n1 n2) :on-mouse-up note-off3} (str n1 " " n2)])

(defn button-row [n]
  [:span (map #(note-button3 n %) (range 10 100 10))
    [:br]])

(defn button-grid [n]
  (map #(button-row %) (range 10 n 10)))

(defn main []
   [:div [:h2 "Welcome to reagent_demo"]
    [:div [:a {:href "#/about"} "go to about page"]]
    (button-grid 100)
    ])



; (defn main []
;   (let [color (atom "red")]
;     (fn []
;       (js/setTimeout #(swap-color! color) 500)
;        [:div [:h2 {:style {:color @color} :on-click #(js/alert "asdf")} "Welcome to reagent_demo"]
;         [:div [:a {:href "#/about"} "go to about page"]]
;         (note-button 44)
;         (note-button 55)])))

; (defn main []
;   (let [seconds (atom 0)
;         click-count (atom 0)]
;     (fn []
;       (js/setTimeout #(swap! seconds inc) 1000)
;       [:div
;        "Seconds Elapsed: " @seconds
;        [:div {:on-click #(swap! click-count inc)}
;         "I have been clicked " @click-count " times."]])))
