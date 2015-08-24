(ns reagent_demo.views.about_page)

(defn main []
  [:div [:h2 "About "]
    [:div [:a {:href "#/"} "go to the home page"]]])

(def ac (new window/AudioContext))

(defn play-clip [url position duration rate gain reverse]
  (let [xhr (new window/XMLHttpRequest)
        source (.createBufferSource ac)
        gain-node (.createGain ac)
        biquad-node (.createBiquadFilter ac)]
    (.open xhr "get" url true)
    (set! (.-responseType xhr) "arraybuffer")
    (set! (.-onload xhr) (fn []
      (.decodeAudioData ac (.-response xhr)
        (fn [buffer]
          (when reverse
            (js/Array.prototype.reverse.call (.getChannelData buffer 0))
            (js/Array.prototype.reverse.call (.getChannelData buffer 1)))
          ; (.linearRampToValueAtTime (.-gain gain-node) 1 0)
          ; (.linearRampToValueAtTime (.-gain gain-node) 0 5)
          (set! (.-buffer source) buffer)
          (set! (.-value (.-gain gain-node)) gain)
          (set! (.-value (.-playbackRate source)) rate)
          ; (set! (.-Q biquad-node) 1)
          ; (set! (.-gain biquad-node) 1)
          ; (set! (.-type biquad-node) "notch")
          ; (set! (.-value (.-frequency biquad-node)) 1000)
          (.connect source biquad-node)
          (.connect biquad-node gain-node)
          (.connect gain-node (aget ac "destination"))
          (.start source (.-currentTime ac) position)
          (js/setTimeout
            #(.stop source 0)
            (* duration 1000))))))
    (.send xhr)))

(def url "http://static.kevvv.in/sounds/callmemaybe.mp3")

(defn n-loops [times seconds func]
  (when (< 0 times)
    (func times)
    (js/setTimeout
      #(n-loops (dec times) seconds func)
      (* 1000 seconds)
    )
  )
)

(n-loops 15 5 #(play-clip url (- 16 %) 10 % 1 (odd? %)))
