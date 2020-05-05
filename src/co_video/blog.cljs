(ns co-video.blog
  (:require
    [reagent.core :as r]
    [reagent.dom :as d]
    [clojure.string]))



(defn home-page[]
  [:div
   [:h2 "Welcome to" [:div {:color "#FF0000"} "Husain.ai"] "blog"]
   [:li "about"]
   [:li "blog"]
   [:li "youtube"]])

(defn mount-blog []
    (d/render [home-page] (.getElementById js/document "app")))

(defn init! [] (mount-blog))
