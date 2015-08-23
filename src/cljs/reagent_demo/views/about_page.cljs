(ns reagent_demo.views.about_page)

(defn main []
  [:div [:h2 "About "]
    [:div [:a {:href "#/"} "go to the home page"]
    (player)]])

(defn player []
  [:div "this is a div"])

(def audio-context
  (if window/webkitAudioContext
    (new window/webkitAudioContext)
    window/AudioContext))

(defn play-sound [buffer]
  (let [source (.createBufferSource source)]
    (aset source "buffer" buffer)
    (.connect source (aget audio-context "destination"))
    (.start source 0)))

(defn local-file->chan [file]
  (let [reader (new window/FileReader)
        resp-c (chan)
        c (chan)]
    (set! (.-onload reader)
      (fn [] (put! resp-c (.-result reader))))
    (.readAsArrayBuffer reader file)
    (go-loop (let [resp (<! resp-c)]
               (.decodeAudioData audio-context
                                 resp
                                 #(put! c %))))
    c))

(defn load-sound [file callback]
  (let [reader (FileReader.)]
    (aset reader "onload"
      (fn [] (.decodeAudioData audio-context
        (aget reader "result")
        #(callback buffer))))
    (.readAsArrayBuffer reader file)))

; (def contexts
;   [(new window/webkitAudioContext)
;     (new window/webkitAudioContext)
;     (new window/webkitAudioContext)
;     (new window/webkitAudioContext)
;     (new window/webkitAudioContext)])

(defn play-clip [url, position, duration]
  (let [ac (new window/AudioContext)
        xhr (new window/XMLHttpRequest)]
    (.open xhr "get" url true)
    (set! (.-responseType xhr) "arraybuffer")
    (set! (.-onload xhr)
      (fn []
        (.decodeAudioData ac (.-response xhr)
          (fn [buffer]
            (let [source (.createBufferSource ac)]
              (set! (.-buffer source) buffer)
              (.connect source (aget ac "destination"))
              (.start source (.-currentTime ac) position)
              (.setTimeout js/window
                #(.stop source 0)
                (* duration 1000)))))))
    (.send xhr)))

(def url "http://static.kevvv.in/sounds/callmemaybe.mp3")
(play-clip url 3 5)

; x = 0
; setInterval(->
;   playClip(url, x * 2, 0.75)
;   x += 1
; , 500)
