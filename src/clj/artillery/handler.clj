(ns artillery.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [artillery.middleware :refer [wrap-middleware]]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as d]
            [artillery.orchestrator :as orchestrator]
            [config.core :refer [env]]))

(defn wrap-handler [cmpt handler]
  (fn [request]
    (merge (select-keys cmpt [:semaphore :orchestrator])
           request)))

(def json-header {"Content-Type" "application/json"})

(defn get-scenes [request]
  (d/chain (orchestrator/get-scenes (:orchestrator request))
           (fn [scenes] {:status 200
                         :headers json-header
                         :body scenes})))

(defroutes routes
  (GET "/apli/scenes" get-scenes)
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))

(defrecord Handler [http-handler semaphore orchestrator]
  component/Lifecycle

  (start [this]
    (->> (app-routes)
         (wrap-handler this)
         (assoc this :http-handler)))

  (stop [this]
    (assoc this :http-handler nil)))

(defn new-handler []
  (map->Handler {:semaphore (d/deferred)}))
