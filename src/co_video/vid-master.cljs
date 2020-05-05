(ns co-video.vid-master
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [reagent.core :as r]
    [reagent.dom :as d]
    [clojure.string :as str]
    [cljsjs.hls]
    [clojure.string]))

;; -------------------------
;; Views

(def state
  (r/atom   {:uuid         (str (cljs.core/random-uuid))
             ; no need for an atom here
             :url          "/vids/300.m3u8"
             :play?        true
             :time-seconds 757.322
             :update-task-num nil
             }))

(defn videoEl [] (.getElementById js/document "video"))

(defn update-state-from-videoEl[]
  (let [videoEl (videoEl)
        currentTime (.-currentTime videoEl)
        playing-status (not (.-paused videoEl))
        ]
    (swap! state assoc :time-seconds currentTime)
    (swap! state assoc :play? playing-status)
    ))

(def update-url (str js/window.location.origin "/update-state"))

(defn stream-status[]
  (update-state-from-videoEl)
  (go (count (<! (http/post update-url {:edn-params @state}))))
  )

(defonce hls (new js/Hls))


(defn on-error [event data]
  (js/console.log (pr-str [event data]))
  (js/alert "on error event happened :( see logs "))

(defn on-level-switch [event data]
  (js/console.log (pr-str [event data]))
  (js/alert "changing levels not implemented yet, see console logs"))

(defn on-parsed [event data]
  (js/console.log "playing/pausing video")
  (.play (videoEl)))

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
      [:div.state (str "state = " (pr-str @state))])
    [:div [:h1 {:style "red"} " HLS video streaming not supported"]]))

;; -------------------------
;; Initialize app

(defn master-ctl []
  [:div
   [:h2 "Co-vid master 👨‍🏫 service "]
   [:video#video
    {:hidden   false
     :height   "100%"
     :width    "100%"
     :controls true
     :src      ""}]
   [:div.input-ctl
    [:input#url
      {:size 50
        :type  "text"}]
     [:input
      {:type     "button"
       :value    "Start Movie 🎥"
       :on-click (fn []
                   (swap! state assoc :url
                          (.-value (.getElementById js/document "url")))
                   (video-player)
                   (swap! state assoc :update-task-num (js/setInterval stream-status 100))
                   )}]]
   [:div
    (let [client-url (str (first (str/split js/window.location.href #"\?")) "?" (:uuid @state))]
      [:li [:a {:href client-url} client-url]])
    [:li (pr-str @state)]]
   ])

(defn mount-root []
  (d/render [master-ctl] (.getElementById js/document "app")))

(defn init! []
  (mount-root))


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
