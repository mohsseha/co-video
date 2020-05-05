(ns co-video.prod
  (:require
    [co-video.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
