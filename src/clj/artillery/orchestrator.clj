(ns artillery.orchestrator
  (:require [com.stuartsierra.component :as component]
            [clojure.data.json :as json]
            [manifold.deferred :as d]
            [artillery.data.scene :as scene]))

(defrecord Orchestrator [db]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this))

(defn new-orchestrator []
  (map->Orchestrator {}))

(defn get-scenes [orchestrator]
  (d/chain (scene/get-scenes (:db orchestrator))
           json/write-str))

(comment
  (use 'artillery.orchestrator :reload)

  (in-ns 'artillery.orchestrator)

  (defonce system (atom {}))

  (reset! system (component/system-map
                  :db (artillery.data.db/new-database)
                  :orchestrator (component/using
                                 (new-orchestrator)
                                 [:db])))

  (swap! system component/start)

  )
