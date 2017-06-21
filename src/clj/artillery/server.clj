(ns artillery.server
  (:require [artillery.handler :refer [app]]
            [config.core :refer [env]]
            ;;[ring.adapter.jetty :refer [run-jetty]]
            )
  (:gen-class))

 (defn -main [& args]
   1)
;;   (let [port (Integer/parseInt (or (env :port) "3000"))]
;;     (run-jetty app {:port port :join? false})))
