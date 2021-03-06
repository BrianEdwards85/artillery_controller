(ns artillery
  (:require [artillery.handler :as handler]
            [artillery.orchestrator :as orchestrator]
            [artillery.data.db :as db]
            [artillery.server :as server]
            [artillery.mqtt :as mqtt]
             [clojure.tools.nrepl.server :as nrepl]
            [com.stuartsierra.component :as component])
  (:gen-class))

(defonce system (atom nil))

(defn create-system []
  (component/system-map
   :db (db/new-database)
   :mqtt (mqtt/new-mqtt)
   :orchestrator (component/using (orchestrator/new-orchestrator) [:db :mqtt])
   :handler (component/using (handler/new-handler) [:orchestrator])
   :server (component/using (server/new-server) [:handler])
   ))

(defn -main [& args]
  (let [n (nrepl/start-server :port 7888)]
    (reset! system (create-system))
    (swap! system component/start)
    (handler/wait (:handler @system))
    (swap! system component/stop)
    (nrepl/stop-server n)
    (System/exit 0))
  )

(comment
  (use 'artillery :reload)

  (in-ns 'artillery)

  (reset! system (create-system))

  (swap! system component/start)

  (swap! system component/stop)

  (defn fire-event [e]
    (println "F:" (:device e) " " (:addr e) " " (:pin e)))

  (defn schedule-job [e]
    (let [pool (overtone.at-at/mk-pool)
          n    (now)]
      (doall (map
              #(overtone.at-at/at
                (+ n (* 1000 (:offset %1)))
                (fn [] (fire-event %1))
                pool)
              e))))


  )

