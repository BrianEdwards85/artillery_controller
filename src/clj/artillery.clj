(ns artillery
  (:require [artillery.handler :as handler]
            [artillery.orchestrator :as orchestrator]
            [artillery.data.db :as db]
            [artillery.server :as server]
            [com.stuartsierra.component :as component])
  (:gen-class))



(defn -main [& args]
  1)

(comment
  (use 'artillery :reload)

  (in-ns 'artillery)

  (defonce system (atom (component/system-map
                         :db (db/new-database)
                         :orchestrator (component/using (orchestrator/new-orchestrator) [:db])
                         :handler (component/using (handler/new-handler) [:orchestrator])
                         :server (component/using (server/new-server) [:handler]))))

  (swap! system component/start)

  (swap! system component/stop)

  )

