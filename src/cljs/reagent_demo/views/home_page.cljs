(ns reagent_demo.views.home_page
  (:require [reagent.core :as reagent :refer [atom]]
            [hum.core :as hum]))

(def ctx (hum/create-context))
(def vco (hum/create-osc ctx :square))
(def vco2 (hum/create-osc ctx :sine))
(def vcf (hum/create-biquad-filter ctx))
(def vcf2 (hum/create-biquad-filter ctx))
(def output (hum/create-gain ctx))
(def output2 (hum/create-gain ctx))

(hum/connect vco vcf)
(hum/connect vcf output)
(hum/start-osc vco)
(hum/connect-output output)

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

(defn note-on [a b]
  (hum/note-on output vco (note-to-frequency a))
  (hum/note-on output2 vco2 (note-to-frequency b)))

(defn note-off []
  (hum/note-off output)
  (hum/note-off output2))

(def timeout-atom (atom []))

(defn delayed-off []
  (js/clearTimeout @timeout-atom)
  (reset! timeout-atom (js/setTimeout note-off 300)))

(defn rand-hex-char []
  (char (rand-nth (concat (range 48 58) (range 66 72)))))

(defn rand-hex []
  (str "#" (rand-hex-char) (rand-hex-char) (rand-hex-char)))

(defn hex-char [n]
  (let [chars (concat (range 48 58) (range 66 71))]
    (char (nth chars (quot n 5)))))

(defn make-hex [a b]
  (str "#" (hex-char a) (hex-char b) 7))

(defn button-style [a b]
  {:width "25px" :height "25px" :font-size "12px" :outline 0 :border 0 :background-color (make-hex a b)})

(defn note-button [n1 n2]
  [:button {:key (rand) :style (button-style n1 n2) :on-mouse-down #(note-on n1 n2) :on-mouse-up delayed-off}])

(def grid-range (range 5 70 5))

(defn button-row [n]
  [:div {:key (rand)} (map #(note-button n %) grid-range) [:br]])

(defn button-grid []
  (map #(button-row %) grid-range))

(defn main []
   [:div [:h2 "Welcome to reagent_demo"]
    [:div [:a {:href "#/about"} "go to about page"]]
    [:br]
    (button-grid)])

;
; (defn swap-color! [color]
;   (reset! color (rand-hex)))

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
