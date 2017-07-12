(ns artillery.mqtt
  (:require [clojurewerkz.machine-head.client :as mh]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]))

(defrecord MQTT [connection]
  component/Lifecycle

  (start [this]
    (let [id (mh/generate-id)
          url
          "tcp://192.168.16.131:1883" ]
;;          "tcp://localhost:1883"]
      (assoc this :connection (mh/connect url id))))

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

