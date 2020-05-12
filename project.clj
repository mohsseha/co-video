(defproject co-video "0.1.0-SNAPSHOT"
  :description
  "Co-Video a util to share HLS video streams across the internet and keep them in  sync"
  :license
  {:name "Eclipse Public License"
   :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.10.1"]
;                 [clj-http-lite "0.3.0"]
                 [cljsjs/hls "0.12.2-0"]
                 [org.clojure/clojurescript "1.10.597"]
                 [compojure "1.6.1"]
                 [http-kit "2.3.0"]
                 [cljs-http "0.1.46"]
                 [reagent "0.10.0"]]
  :main co-video.webserver

  :plugins
  [[lein-cljsbuild "1.1.7"]
   [lein-figwheel "0.5.19"]]

  :clean-targets
  ^{:protect false}

  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :resource-paths ["public"]

  :figwheel
  {:http-server-root "."
   :nrepl-port       7002
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
   :css-dirs         ["public/css"]}

  :cljsbuild
  {:builds {:app
            {:source-paths ["src" "env/dev/cljs"]
             :compiler
             {:main          "co-video.dev"
              :output-to     "public/js/app.js"
              :output-dir    "public/js/out"
              :asset-path    "js/out"
              :source-map    true
              :optimizations :none
              :pretty-print  true}
             :figwheel
             {:on-jsload "co-video.core/mount-root"
              ;                         :open-urls ["http://localhost:3449/index.html"]}}
              :open-urls ["http://localhost/index.html"]}}

            :release
            {:source-paths ["src" "env/prod/cljs"]
             :compiler
             {:output-to     "public/js/app.js"
              :output-dir    "target/release"
              :optimizations :advanced
              :infer-externs true
              :pretty-print  false}}}}

  :aliases {"package" ["do" "clean" ["cljsbuild" "once" "release"]]}

  :profiles
  {:dev {:source-paths ["src" "env/dev/clj"]
         :dependencies [[binaryage/devtools "1.0.0"]
                        [figwheel-sidecar "0.5.19"]
                        [nrepl "0.6.0"]
                        [cider/piggieback "0.4.2"]]}})
