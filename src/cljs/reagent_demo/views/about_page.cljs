(ns reagent_demo.views.about_page)

(defn main []
  [:div [:h2 "About "]
    [:div [:a {:href "#/"} "go to the home page"]]])

(def ac (new window/AudioContext))

(defn play-clip [url, position, duration]
  (let [xhr (new window/XMLHttpRequest)]
    (.open xhr "get" url true)
    (set! (.-responseType xhr) "arraybuffer")
    (set! (.-onload xhr) (fn []
      (.decodeAudioData ac (.-response xhr)
        (fn [buffer]
          (let [source (.createBufferSource ac)]
            (set! (.-buffer source) buffer)
            (.connect source (aget ac "destination"))
            (.start source (.-currentTime ac) position)
            (js/setTimeout
              #(.stop source 0)
              (* duration 1000)))))))
    (.send xhr)))

(def url "http://static.kevvv.in/sounds/callmemaybe.mp3")
; (play-clip url 3 100)

; (js/setInterval #(play-clip url 7.1 30) 4000)

; (play-clip url 7.1 0.35)
; (js/setInterval #(play-clip url 7.1 2) 2000)

; (js/setInterval #(play-clip url 17.1 2.5) 1350)
