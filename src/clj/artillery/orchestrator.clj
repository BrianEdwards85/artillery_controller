(ns artillery.orchestrator
  (:require [com.stuartsierra.component :as component]
            [clojure.data.json :as json]
            [manifold.deferred :as d]
            [artillery.data.events :as events]
            [artillery.data.scene :as scene]

            [clojure.core.async :as async]
            [overtone.at-at :as at]
            [manifold.stream :as s]

            ))

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
  (d/chain
   (if (contains? event :idx)
     (d/chain (events/push-scene-events (:db orchestrator) (:scene event) (:idx event))
              (fn [_] (events/insert-scene-event (:db orchestrator) event)))
     (events/append-scene-event (:db orchestrator) event))
           (fn [_] 1 )))

(defn remove-scene-event [orchestrator scene idx]
  (d/chain (events/remove-scene-event (:db orchestrator) scene idx)
           (fn [_] 1 )))

(defn schedule-events [orchestrator scene]
  (let [r (s/stream)]
    (do
      (async/go
        (let [events @(d/chain (events/get-scene-events (:db orchestrator) scene)
                               add-scene-event-abs)
              pool (at/mk-pool)
              n (at/now)]
          (at/at
           (->> events (map :offset) (apply max) (* 1100) (+ n))
           #(s/put! r "DONE")
           pool
           )
          (doall
           (map
            #(at/at
              (+ n (* 1000 (:offset %1)))
              (fn [] (do
                       (println (str "Fire: " (:idx %1)))
                       (s/put! r %1)))
              pool)
            events
            ))))
      r
    )
  )
)

(comment

  (manifold.stream/consume #(println %) (artillery.orchestrator/schedule-events (:orchestrator @system) "bb0daed2-6760-481e-8de5-53c6d55bf85e" ) )

  )
