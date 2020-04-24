(ns ai.husain.main
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :refer [run-server]]))


(defroutes my-blog
;  (GET "/" [] "https://learnxinyminutes.com/docs/compojure/")
  (route/files "/")
  )

(defn -main [& args]
  (println "starting server")
  ;  (println (str args))
  (run-server my-blog
              {:port 80}))