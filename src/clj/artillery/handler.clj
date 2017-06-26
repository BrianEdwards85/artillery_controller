(ns artillery.handler
  (:require [compojure.core :refer [GET PUT DELETE routes]]
            [compojure.route :refer [not-found resources]]
            [artillery.middleware :refer [wrap-middleware]]
            [artillery.orchestrator :as orchestrator]
            [artillery.http :as http]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as d]
            [config.core :refer [env]]
            [clojure.data.json :as json]))

(defn wrap-handler [cmpt handler]
  (fn [request]
    (handler
     (merge (select-keys cmpt [:semaphore :orchestrator])
            request))))

(def json-header {"Content-Type" "application/json"})

(defn get-scenes [request]
  (d/chain (orchestrator/get-scenes (:orchestrator request))
           (fn [scenes] {:status 200
                         :headers json-header
                         :body scenes})))
(defn add-scene [request]
  (let [description (-> request :body slurp)]
    (d/chain (orchestrator/add-scene (:orchestrator request) description)
             #(hash-map :status 200
                        :headers json-header
                        :body %))))

(defn get-scene-events [request]
  (let [scene (-> request :route-params :scene)]
    (d/chain (orchestrator/get-scene-events (:orchestrator request) scene)
             #(hash-map :status 200
                        :headers json-header
                        :body %))))

(defn add-scene-event [request]
  (let [scene (-> request :route-params :scene)
        event (-> request :body slurp (json/read-str :key-fn keyword) (assoc :scene scene))]
    (d/chain
     (orchestrator/add-scene-event (:orchestrator request) event)
     #(hash-map :status 200
                :body (str %)))))

(defn remove-scene-event [request]
  (let [scene (-> request :route-params :scene)
        idx (-> request :route-params :idx)]
    (d/chain
     (orchestrator/remove-scene-event (:orchestrator request) scene idx)
     #(hash-map :status 200
                :body (str %)))))

(defn app-routes []
  (routes
   (GET "/" [] (http/loading-page))
   (GET "/scene/:id" [id] (http/loading-page))
   (GET "/api/scenes" [] get-scenes)
   (PUT "/api/scenes" [] add-scene)
   (GET "/api/scenes/:scene" [scene] get-scene-events)
   (PUT "/api/scenes/:scene" [scene] add-scene-event)
   (DELETE "/api/scenes/:scene/:idx" [scene idx] remove-scene-event)
   (resources "/")
   (not-found "Not Found")))

;;(def app (wrap-middleware #'routes))

(defrecord Handler [http-handler semaphore orchestrator]
  component/Lifecycle

  (start [this]
    (->> (app-routes)
         wrap-middleware
         (wrap-handler this)
         (assoc this :http-handler)))

  (stop [this]
    (assoc this :http-handler nil)))

(defn new-handler []
  (map->Handler {:semaphore (d/deferred)}))
