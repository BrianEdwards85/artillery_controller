(ns artillery.data.events
  (:require [artillery.data.db :refer [get-connection]]
            [manifold.deferred :as d]
            [yesql.core :refer [defqueries]]))

(defqueries "sql/events.sql")
;;get-scene-events-sql
;;add-scene-event<!

(defn get-scene-events [db scene]
  (d/future (get-scene-events-sql {:scene scene} (get-connection db))))

(defn add-scene-event [db event]
  (d/future (add-scene-event<! event (get-connection db))))
