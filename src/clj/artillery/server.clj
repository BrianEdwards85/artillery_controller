(ns artillery.server
  (:require ;;[artillery.handler :refer [app]]
            ;;[config.core :refer [env]]
            [aleph.http :as http]
            [com.stuartsierra.component :as component]
            ;;[ring.adapter.jetty :refer [run-jetty]]
            )
  (:gen-class))

(defrecord Server [handler server]
  component/Lifecycle

  (start [this]
     (assoc this
            :server (http/start-server (:http-handler handler) {:port 8080}))
     )

  (stop [this]
    (.close server)
    (assoc this :server nil)) 

  )

(defn new-server []
  (map->Server {}))

;;   (let [port (Integer/parseInt (or (env :port) "3000"))]
;;     (run-jetty app {:port port :join? false})))
