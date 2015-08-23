(ns reagent_demo.views.about_page)

(defn main []
  [:div [:h2 "About "]
    [:div [:a {:href "#/"} "go to the home page"]]])

(def ac (new window/AudioContext))

(defn play-clip [url, position, duration, rate, reverse]
  (let [xhr (new window/XMLHttpRequest)]
    (.open xhr "get" url true)
    (set! (.-responseType xhr) "arraybuffer")
    (set! (.-onload xhr) (fn []
      (.decodeAudioData ac (.-response xhr)
        (fn [buffer]
          (let [source (.createBufferSource ac)]
            (when reverse
              (js/Array.prototype.reverse.call (.getChannelData buffer 0))
              (js/Array.prototype.reverse.call (.getChannelData buffer 1)))
            (set! (.-value (.-playbackRate source)) rate)
            (set! (.-buffer source) buffer)
            (.connect source (aget ac "destination"))
            (.start source (.-currentTime ac) position)
            (js/setTimeout
              #(.stop source 0)
              (* duration 1000)))))))
    (.send xhr)))

(def url "http://static.kevvv.in/sounds/callmemaybe.mp3")
(play-clip url 2 100 0.5 true)
(play-clip url 0 100 0.25 false)

; (js/setInterval #(play-clip url 7.1 30) 4000)

; (play-clip url 7.1 0.35)
; (js/setInterval #(play-clip url 7.1 2 2 false) 2000)

; (js/setInterval #(play-clip url 17.1 2.5) 1350)

; Array.prototype.reverse.call(buffer.getChannelData(0))
;       Array.prototype.reverse.call(buffer.getChannelData(1))
