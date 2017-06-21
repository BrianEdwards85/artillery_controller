(ns artillery.data.scene
  (:require [artillery.data.db :refer [get-connection]]
            [manifold.deferred :as d]
            [yesql.core :refer [defqueries]]))

(defqueries "sql/scene.sql")

(defn add-scene [db description]
  (let [i (str (java.util.UUID/randomUUID))
        r (d/future (insert-scene<! {:id i :description description} (get-connection db)))]
    (d/chain r (fn [x] i))))

(defn get-scenes [db]
  (d/future (get-scenes-sql {} (get-connection db))))

