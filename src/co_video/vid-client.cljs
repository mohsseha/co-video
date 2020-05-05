(ns co-video.vid-client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [reagent.core :as r]
    [reagent.dom :as d]
    [cljsjs.hls]
    [clojure.string]))

;; -------------------------
;; Views

(def state
  (r/atom
   {:uuid         nil
    :url          "/vids/300.m3u8"
    :play?        true
    :hidden?      true
    :time-seconds 757.322}))


(defn update-url [uuid] (str js/window.location.origin "/state/" uuid))

(defn update-state-from-server []
  (if (:uuid @state)
    (go
     (let [response (<! (http/get (update-url (:uuid @state))))
           result   (cljs.reader/read-string (:body response))]
       (reset! state (merge @state result))))))

(defn videoEl [] (.getElementById js/document "video"))

(defonce hls (new js/Hls))


(defn update-player-ctls []
  (let [seconds     (:time-seconds @state)
        play-state  (:play? @state)
        videoEl     (videoEl)
        currentTime (.. videoEl -currentTime)
        diff-secs   (js/Math.abs (- seconds currentTime))]
    (if (> diff-secs 5)
      (set! (.. videoEl -currentTime) seconds))
    (js/console.log (str "DEBUG: 2" play-state " \t" currentTime "\t" seconds))
    (if play-state (.play videoEl) (.pause videoEl))
    (js/console.log "DEBUG: 3")))



; derived from https://github.com/alkerway/hls-cljs/blob/master/src/hls_cljs/core.cljs :
(defn on-error [event data]
  (js/console.error (pr-str [event data])))

(defn on-level-switch [event data]
  (js/console.warn (pr-str [event data])))

; The following is essentially bypassing the react structure. It's because of the way HLS interacts with the elements.
; There maybe a way of doing this using react but I don't have time to figure that out :)
(defn on-parsed [event data]
  (js/console.log "DEBUG:HLS parsed! ")
  (update-player-ctls)
  (add-watch state :update-player-ctls update-player-ctls)
  )

(defn add-listeners [hls]
  (let [evts (js->clj (.-Events js/Hls) :keywordize-keys true)]
    (.on hls (:LEVEL_SWITCHED evts) on-level-switch)
    (.on hls (:ERROR evts) on-error)
    (.on hls (:MANIFEST_PARSED evts) on-parsed)))


(defn video-player[]
  (if (js/Hls.isSupported)
    (let [videoEl (videoEl)]
      (.loadSource hls (:url @state))
      (.attachMedia hls videoEl)
      (add-listeners hls))
    (js/alert "HLS is not supported on your browswer")))

;; -------------------------
;; Initialize app

(defn home-page []
  [:div
   [:h2 "Welcome to Husain.ai Co-vid service "]
   [:li (pr-str @state)]
   [:li
    [:input
     {:type     "button"
      :value    "join-theater"
      :on-click (fn []
                  (swap! state assoc :hidden? false)
                  (video-player))}]]
   [:video#video
    {:hidden   (:hidden? @state)
     :height   "100%"
     :width    "100%"
     :controls true
     :src      (:url @state)}]])


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (let [url-str (subs js/window.location.search 1)]
    (js/console.log (str "DEBUG\t" url-str))
    (js/console.log (str "State2\t" @state))
    (swap! state assoc :uuid url-str)
    (js/console.log (str "State3\t" @state))
    (mount-root)
    (add-watch state :logger #(-> %4 clj->js js/console.log))
        (swap! state assoc :update-task-num (js/setInterval update-state-from-server 800))
    ))
