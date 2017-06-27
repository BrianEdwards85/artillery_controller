(ns artillery.mqtt
  (:require [clojurewerkz.machine-head.client :as mh]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]))

(defrecord MQTT [connection]
  component/Lifecycle

  (start [this]
    (let [id (mh/generate-id)]
      (assoc this :connection (mh/connect "tcp://192.168.16.131:1883" id))))

    (stop [this]
          (do
            (mh/disconnect connection)
            (assoc this :connection nil))))

(defn new-mqtt []
  (map->MQTT {}))

(defn publish [mqtt topic payload]
  (mh/publish
   (:connection mqtt)
   topic
   (json/write-str payload)))

