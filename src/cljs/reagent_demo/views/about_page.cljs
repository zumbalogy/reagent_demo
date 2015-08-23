(ns reagent_demo.views.about_page)

(defn main []
  [:div [:h2 "About "]
    [:div [:a {:href "#/"} "go to the home page"]]])

(def ac (new window/AudioContext))

(defn distortion-curve [amount]
  (let [k (+ amount 3)
        pi (.-PI js/Math)
        n-samples 44100
        deg (/ pi 180)]
    (map
      #(let [x (/ (* 2 %) (- n-samples 1))]
          (/ (* k x 20 deg)
          (* (.abs js/Math x) (+ amount pi))))
      (range 44100))))

(defn play-clip [url position duration rate gain reverse]
  (let [xhr (new window/XMLHttpRequest)
        source (.createBufferSource ac)
        gain-node (.createGain ac)
        analize-node (.createAnalyser ac)
        distort-node (.createWaveShaper ac)
        biquad-node (.createBiquadFilter ac)
        convolve-node (.createConvolver ac)]
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
          ; (set! (.-buffer convolve-node) buffer)
          (set! (.-value (.-gain gain-node)) gain)
          (set! (.-value (.-playbackRate source)) rate)
          (set! (.-buffer source) buffer)
          (set! (.-type biquad-node) "notch")
          (set! (.-value (.-frequency biquad-node)) 1000)
          (set! (.-Q biquad-node) 1)
          (set! (.-gain biquad-node) 1)
          (set! (.-curve distort-node) (distortion-curve 400))
          (set! (.-oversample distort-node) "4x")
          (.connect source analize-node)
          (.connect analize-node distort-node)
          (.connect distort-node biquad-node)
          (.connect biquad-node gain-node)
          ; (.connect biquad-node convolve-node)
          ; (.connect convolve-node gain-node)
          (.connect gain-node (aget ac "destination"))
          (.start source (.-currentTime ac) position)
          (js/setTimeout
            #(.stop source 0)
            (* duration 1000))))))
    (.send xhr)))

(def url "http://static.kevvv.in/sounds/callmemaybe.mp3")
; (play-clip url 0 100 0.4 true)
; (play-clip url 0 100 0.25 1 false)
(play-clip url 3 6 1 1 false)

; (js/setInterval #(play-clip url 7.1 4 1 1 false) 3000)

; (play-clip url 7.1 0.35)
; (js/setInterval #(play-clip url 7.1 2 2 false) 2000)

; (js/setInterval #(play-clip url 17.1 2.5) 1350)

; Array.prototype.reverse.call(buffer.getChannelData(0))
;       Array.prototype.reverse.call(buffer.getChannelData(1))
