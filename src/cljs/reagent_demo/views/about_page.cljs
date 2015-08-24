(ns reagent_demo.views.about_page)

(defn main []
  [:div [:a {:href "#/"} "go to the home page"]])

(def ac (new window/AudioContext))
(def int-array (new js/Uint8Array 2048))

(defn event-processor [event]
  (let [input-buffer (.-inputBuffer event)
        output-buffer (.-outputBuffer event)
        input-data-0 (.getChannelData input-buffer 0)
        output-data-0 (.getChannelData output-buffer 0)
        ]
    (for [x (range (.-length input-buffer))]
      (aset (.getChannelData output-buffer 0) x (aget input-data-0 x))
    )
  )
)

(defn play-clip [url position duration rate gain reverse]
  (let [xhr (new window/XMLHttpRequest)
        source (.createBufferSource ac)
        analize-node (.createAnalyser ac)
        gain-node (.createGain ac)
        process-node (.createScriptProcessor ac 4096 2 1)
        biquad-node (.createBiquadFilter ac)]
    (.open xhr "get" url true)
    (set! (.-responseType xhr) "arraybuffer")
    (set! (.-onload xhr) (fn []
      (.decodeAudioData ac (.-response xhr)
        (fn [buffer]
          (when reverse
            (js/Array.prototype.reverse.call (.getChannelData buffer 0))
            (js/Array.prototype.reverse.call (.getChannelData buffer 1)))
          ; (.linearRampToValueAtTime (.-gain gain-node) 1 position)
          ; (.linearRampToValueAtTime (.-gain gain-node) 0 0)
          (set! (.-buffer source) buffer)
          (set! (.-value (.-gain gain-node)) gain)
          (set! (.-value (.-playbackRate source)) rate)
          (set! (.-onaudioprocess process-node) event-processor)
          ; (set! (.-Q biquad-node) 1)
          ; (set! (.-gain biquad-node) 1)
          ; (set! (.-type biquad-node) "notch")
          ; (set! (.-value (.-frequency biquad-node)) 1000)
          (.connect source analize-node)
          (.connect analize-node process-node)
          ; (.connect analize-node biquad-node)
          (.connect process-node biquad-node)
          (.connect biquad-node gain-node)
          (.connect gain-node (aget ac "destination"))
          (.start source 0 position)
          (js/setInterval #(do
              (.getByteTimeDomainData analize-node int-array)
              ; (.log js/console (aget int-array 0)))
              )
            50)
          (js/setTimeout
            #(.stop source 0)
            (* duration 1000))))))
    (.send xhr)))

(def url "http://static.kevvv.in/sounds/callmemaybe.mp3")
(def url2 "https://ia600805.us.archive.org/27/items/NeverGonnaGiveYouUp/jocofullinterview41.mp3")

(play-clip url 15 15 1 1 false)


(defn n-loops [times seconds func]
  (when (< 0 times)
    (func times)
    (js/setTimeout
      #(n-loops (dec times) seconds func)
      (* 1000 seconds))))

; (n-loops 16 3 #(play-clip url (- 20 %) 5 (if (even? %) 2 0.5) 1 (odd? %)))
; (n-loops 16 3 #(play-clip url2 (- 20 %) 5 (if (even? %) 2 0.5) 1 (odd? %)))
