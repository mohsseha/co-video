(ns husain.ai.prod
  (:require [husain.ai.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
