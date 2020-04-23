(ns ai.husain.core
  (:require
    [reagent.core :as r]
    [reagent.dom :as d]
    [cljsjs.hls]
    [clojure.string]))

;; -------------------------
;; Views

(def state
  (r/atom
   {:url          "http://localhost:3449/vids/300.m3u8"
    :play?        true
    :ctl?         true
    :full-screen? false
    :hidden?      true
    ; can you even see the video
    :time-seconds 757}))

(declare debug mount-video)

(defn videoEl [] (.getElementById js/document "video"))

(defonce hls (new js/Hls))

(defn home-page []
  [:div
   [:h2 "Welcome to Husain.ai Co-vid service "]
   ;   [:li "about"]
   ;   [:li "blog"]
   ;   [:li "youtube"]
   [:li
    [:input
     {:type     "button"
      :value    "join-theater"
      :on-click (fn [] (swap! state assoc :hidden? false)
                  (mount-video))}]]
   [:li [:input {:type "button" :value (pr-str @state)}]]
   [:br]
   [:video#video
    {:hidden (:hidden? @state) :height "100%" :width "100%" :controls (:ctl? @state) :src ""}]])


; derived from https://github.com/alkerway/hls-cljs/blob/master/src/hls_cljs/core.cljs :
(defn set-play-at [play-state seconds]
  (let [videoEl (videoEl)]
    (. videoEl pause)
    (set! (.. videoEl -currentTime) seconds)
    (if play-state (.play videoEl) (.pause videoEl))))

(defn on-error [event data]
  (js/console.log (pr-str [event data]))
  (js/alert "on error event happened :( see logs "))

(defn on-level-switch [event data]
  (js/console.log (pr-str [event data]))
  (js/alert "changing levels not implemented yet, see console logs"))

(defn on-parsed [event data]
  (js/console.log "playing video")
  ;  (.startLoad hls (:time-seconds @state))
  (set-play-at (:play? @state) (:time-seconds @state)))


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
      (add-listeners hls)
      [:div
       [:h1 "welcome to movie co-vid, the covid movie theater"]
       [:span (str "state = " (pr-str @state))]])
    [:div [:h1 {:style "red"} " HLS video streaming not supported"]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn mount-video []
  (d/render [video-player] (.getElementById js/document "video")))


(defn init! []
  (let [url-str (clojure.string/lower-case js/window.location.pathname)]
    (if (clojure.string/includes? url-str "v.html")
      (mount-root)
      (mount-root))))


; the main driver:
;(defn debug []
;  (let [hls             (new js/Hls)
;        video           (.getElementById js/document "video")
;        videoSrcInHls   "http://localhost:3449/vids/300.m3u8"
;        MANIFEST_PARSED (.-MANIFEST_PARSED (.-Events js/Hls))]
;    (js/console.log (pr-str video))
;    (if (. js/Hls isSupported)
;      (let []
;        (. hls loadSource videoSrcInHls)
;        (.attachMedia hls video)
;        (.on hls MANIFEST_PARSED
;             (fn []
;               (js/alert "in the on function")
;               (.startLoad hls 60)
;               (comment .play video))))
;      (js/alert "hls not supported")))
;  (js/console.warn "finished init!"))
