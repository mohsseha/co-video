(ns co-video.webserver
    (:require [compojure.core :refer :all]
              [clj-http.client :as client]
              [compojure.route :as route]
              [clojure.spec.alpha :as s]
              [clj-gcloud.storage :as gs]
              [ring.middleware.cors :refer [wrap-cors]]
              [org.httpkit.server :refer [run-server]]))

; half a DB in two statements:)
(def db (atom {}))

; hack to allow this server to proxy a data store like s3 or gs
(def gs-prefix (clojure.string/replace (slurp "config/gs-prefix") "\n" ""))
(def gs-client (gs/init {}))

(add-watch db :watcher-log
           (fn [key atom old-state new-state]
             (if (not= old-state new-state)
               (do
                 ;                 (println "-- DB Changed --\t[[" key "]] watch log:")
                 ;                 (println "old-state" old-state)
                 ;                 (clojure.pprint/pprint (str "atom" @atom))
                 ;                 (println "new-state" new-state)
                 (spit "/tmp/db.edn" new-state)))))

(def uuid-regexp
  #"[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")

; New state spec:
(comment
 {:uuid            "822bcc0e-4161-44bd-866c-9a21defe38ad"
  ; no need for an atom here
  :url             "/vids/300.m3u8"
  :play?           true
  :time-seconds    757.322
  :update-task-num nil})

(s/def ::uuid #(re-matches uuid-regexp %))
(s/def ::url string?)
(s/def ::play? boolean?)
(s/def ::time-seconds number?)
(s/def ::update-task-num (s/or :nothing nil? :task-num int?))
(s/def ::new-state
       (s/keys :req-un [::uuid ::url ::play? ::time-seconds ::update-task-num]))


(defn valid? [state] (s/valid? ::new-state state))

(defn update-state-n-log [req]
  (let [new-state       (clojure.edn/read-string (slurp (:body req)))
        valid-new-state (valid? new-state)
        uuid            (:uuid new-state)]
    (assert valid-new-state)
    (reset! db (merge @db {(:uuid new-state) new-state}))
    ;    (clojure.pprint/pprint (get @db uuid))
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (get @db uuid)}))

(defn get-gs-file [filename]
  (let [url    (str gs-prefix "/" filename)
        blob   (->> url gs/->blob-id (gs/get-blob gs-client))
        stream (->> blob gs/read-channel gs/->input-stream)]
    (println (str "pulling file" url " from gs bucket"))
    stream))

(defn get-file [filename]
  (let [url (str data-store-url filename)]
    (println (str "pulling in the file:" filename "\t fetching url: " url))
    (client/get url {:as :stream :throw-exceptions false})))

(defroutes my-blog
  (GET "/state/:uuid" [uuid]
       (println (str "GET /state/" uuid))
       (println (str "=" (get @db uuid {:error true})))
       (str (get @db uuid {:error true})))
  (GET "/vid/:a{.*}"
       [a]
       (get-file a))

  (POST "/update-state" [req] update-state-n-log)

  (route/files "/")
  (route/not-found "<h1> dead end </h1>"))

(defn -main [& args]
  (println "starting server")
  ;  (println (str args))
  (run-server my-blog
              {:port 80}))
