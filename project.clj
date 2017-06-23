(defproject artillery "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [
                 [org.clojure/clojure "1.9.0-alpha15"]
                 ;;[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/clojurescript "1.9.562"
                  :scope "provided"]
                 [org.clojure/core.async "0.2.395"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojure/tools.logging "0.3.1"]

                 [aleph "0.4.3"]
                 [ring/ring-core "1.6.1"]
                 [ring/ring-defaults "0.3.0"]

                 [com.stuartsierra/component "0.3.2"]
                 [yesql "0.5.3"]
                 [com.h2database/h2 "1.4.194"]
                 [org.clojure/java.jdbc "0.6.1"]

                 [clojurewerkz/machine_head "1.0.0"]

                 [com.cemerick/url "0.1.1"]
                 [reagent "0.6.2"]
                 [reagent-utils "0.2.1"]
                 [re-frame "0.9.2"]
                 [re-com "0.9.0"]
                 [day8.re-frame/http-fx "0.1.3"]

                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [yogthos/config "0.8"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.2.0"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.5"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

;;  :ring {:handler artillery.handler/app
;;         :uberwar-name "artillery.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "artillery.jar"

  :main artillery  ;;.server

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
             {:output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/uberjar"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel {:on-jsload "artillery.core/mount-root"}
             :compiler
             {:main "artillery.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}



            }
   }


;;  :figwheel
;;  {:http-server-root "public"
;;   :server-port 3449
;;   :nrepl-port 7002
;;   :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"
;;                      ]
;;   :css-dirs ["resources/public/css"]
;;   :ring-handler artillery.handler/app}



  :profiles {:dev {:repl-options {:init-ns artillery    ;;.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[ring/ring-mock "0.3.0"]
                                  [ring/ring-devel "1.6.1"]
                                  [prone "1.1.4"]
                                  [figwheel-sidecar "0.5.10"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                                  [pjstadig/humane-test-output "0.8.2"]
                                  ]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.10"]
                             ]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
