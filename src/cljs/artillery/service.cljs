(ns artillery.service
  (:require [ajax.core :as ajax]))

(def json (ajax/json-response-format {:keywords? true}))

(defn get-scenes [succcess failure]
  {:http-xhrio {:method          :get
                :uri             "/api/scenes"
                :response-format json
                :on-success      [succcess]
                :on-failure      [failure]}})

(defn get-scene-events [scene succcess failure]
  {:http-xhrio {:method          :get
                :uri             (str "/api/scenes/" scene)
                :response-format json
                :on-success      [succcess]
                :on-failure      [failure]}})

(defn add-scene-event [event succcess failure]
  (let [body (.stringify js/JSON (clj->js event))]
    {:http-xhrio {:method          :put
                  :uri             (str "/api/scenes/" (:scene event))
                  :body            body
                  :format          (ajax/json-request-format)
                  :headers         {:content-type "application/json"}
                  :response-format json
                  :on-success      [succcess]
                  :on-failure      [failure]}}))

(defn remove-scene-event [event succcess failure]
  {:http-xhrio {:method          :delete
                :uri             (str "/api/scenes/" (:scene event) "/" (:idx event))
                :format          (ajax/json-request-format)
                :response-format json
                :on-success      [succcess]
                :on-failure      [failure]}})
