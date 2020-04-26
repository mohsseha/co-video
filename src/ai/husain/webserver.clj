(ns ai.husain.webserver
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.cors :refer [wrap-cors]]
            [org.httpkit.server :refer [run-server]]))

; half a DB in two statements:)
(def db (atom {}))
(add-watch db :watcher-log
           (fn [key atom old-state new-state]
             (if (not= old-state new-state)
               (do
                 (println "-- DB Changed --\t[[" key "]] watch log:")
                 (println "old-state" old-state)
                 (clojure.pprint/pprint (str "atom" @atom))
                 (println "new-state" new-state)
                 (spit "/tmp/db.edn" new-state)))))

(defn update-state-n-log [req]
  (let [new-state (clojure.edn/read-string (slurp (:body req)))]
    (reset! db (merge @db new-state))
    (clojure.pprint/pprint db)
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    @db}))


(defroutes my-blog
  (GET "/state" req
       (println (keys req))
       @db)
  (POST "/update-state" [req] update-state-n-log)
  (route/files "/")
  (route/not-found "<h1> dead end </h1>"))

(defn -main [& args]
  (println "starting server")
  ;  (println (str args))
  (run-server my-blog
              {:port 80}))