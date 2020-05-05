(ns co-video.core
  (:require
    [reagent.core :as r]
    [reagent.dom :as d]
    [co-video.blog :as blog]
    [co-video.vid-client :as vid-client]
    [co-video.vid-master :as vid-master]
    [clojure.string]))

(def uuid-regexp #"[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")

(defn init! []
  (let [url-str (subs (clojure.string/lower-case js/window.location.search) 1)]
    (cond
      (re-matches uuid-regexp url-str) (vid-client/init!)
      (= "master" url-str) (vid-master/init!)
      :else (blog/init!)
      )
))
