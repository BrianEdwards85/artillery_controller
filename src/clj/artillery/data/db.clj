(ns artillery.data.db
  (:require [clojure.java.jdbc :as sql]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]))

(def db-def {:user        "sa"
             :password    ""
             :classname   "org.h2.Driver"
             :subprotocol "h2"
             :subname     "/tmp/artillery"
             })

(defn get-connection [db]
  {:connection (select-keys db [:connection])})

(defn get-schema-statments [f]
  (->>
   (-> f io/resource io/file slurp (str/split #";"))
   (map str/trim)
   (filter #(-> % empty? not))))

(defn load-schema [conn f]
  (doall
   (map
    #(sql/db-do-commands {:connection  conn} %)
    (get-schema-statments f))))

(defrecord Database [connection]
  component/Lifecycle

  (start [this]
    (let [c (sql/get-connection db-def)]

      (load-schema c "sql/schema.sql")

      (assoc this :connection c)))

  (stop [this]
    (.close connection)
    (assoc this :connection nil))
  )

(defn new-database []
  (map->Database {}))


(comment
  (use 'artillery.data.db :reload)

  (in-ns 'artillery.data.db)

  (def c (sql/get-connection db-def))

  (defonce system (atom {}))

  (reset! system (component/system-map
                  :db (new-database)))

  (swap! system component/start)

  (def db (:db @system))

  )
