(ns reagent_demo.views.home_page)

(def color (atom "red"))

(defn rand-hex-char []
  (char (rand-nth (concat (range 48 58) (range 66 72)))))

(defn rand-hex []
  (str "#" (rand-hex-char) (rand-hex-char) (rand-hex-char)))

(defn interval []
  (reset! color (rand-hex)))

(defonce time-updater (js/setInterval interval 500))

(defn main []
   [:div [:h2 {:style {:color @color}} "Welcome to reagent_demo"]
    [:div [:a {:href "#/about"} "go to about page"]]])
