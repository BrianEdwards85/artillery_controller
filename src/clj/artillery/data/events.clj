(ns artillery.data.events
  (:require [artillery.data.db :refer [get-connection]]
            [manifold.deferred :as d]
            [yesql.core :refer [defqueries]]))

(defqueries "sql/events.sql")
;;get-scene-events-sql
;;add-scene-event<!

(defn get-scene-events [db scene]
  (d/future (get-scene-events-sql {:scene scene} (get-connection db))))

(defn append-scene-event [db event]
  (d/future (append-scene-event<! event (get-connection db))))

(defn insert-scene-event [db event]
  (d/future (insert-scene-event<! event (get-connection db))))

(defn push-scene-events [db scene idx]
  (d/future (push-scene-events! {:scene scene :idx idx} (get-connection db))))

(defn remove-scene-event [db scene idx]
  (d/future (remove-scene-event! {:scene scene :idx idx} (get-connection db))))
