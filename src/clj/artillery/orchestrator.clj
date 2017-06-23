(ns artillery.orchestrator
  (:require [com.stuartsierra.component :as component]
            [clojure.data.json :as json]
            [manifold.deferred :as d]
            [artillery.data.events :as events]
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

(defn add-scene [orchestrator description]
  (d/chain (scene/add-scene (:db orchestrator) description)
           #(json/write-str {:id %
                             :description description})))
(defn add-scene-event-abs [events]
  (loop [rst (rest events)
         c (first events)
         a 0
         ret []]
    (if (nil? c)
      ret
      (let [cabs (+ a (:delay c))]
        (recur (rest rst) (first rst) cabs (conj ret (merge c {:offset cabs})))))))

(defn get-scene-events [orchestrator scene]
  (d/chain (events/get-scene-events (:db orchestrator) scene)
           add-scene-event-abs
           json/write-str))

(defn add-scene-event [orchestrator event]
  (d/chain (events/add-scene-event (:db orchestrator) event)
           (fn [_] 1 )))
