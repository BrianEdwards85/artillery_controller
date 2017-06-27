(ns artillery.handlers
  (:require [re-frame.core :as re-frame]
            [artillery.service :as service]
            [day8.re-frame.http-fx]))

(defn init-db []
  {:page :scenes
   :error nil
   :loading false
   :scene nil
   :scenes nil
   :scene_events []
   })

(defn initialize-event [_ _]
  (init-db))

(defn nav-scenes [{:keys [db]} _]
  {:db (assoc db :page :scenes)
   :dispatch [:get-scenes]})

(defn nav-scene-events [{:keys [db]} [_ s]]
  {:db (assoc db :page :events :scene s)
   :dispatch [:get-scene-events s]})

(defn get-scene-events [{:keys [db]} [_ s]]
  (merge
   (service/get-scene-events s :get-scene-events-succcess :get-scenes-failure)
   {:db (assoc db :loading true)}))

(defn get-scene-events-succcess [db [_ r]]
  (assoc db :loading false :scene_events r))

(defn remove-scene-event [{:keys [db]} [_ e]]
  (merge
   (service/remove-scene-event e :add-scene-event-succcess :get-scenes-failure)
   {:db (assoc db :loading true)}
   ))

(defn add-scene-event [{:keys [db]} [_ e]]
  (merge
   (service/add-scene-event e :add-scene-event-succcess :get-scenes-failure)
   {:db (assoc db :loading true)}))

(defn add-scene-event-succcess [{:keys [db]} _]
  {:dispatch [:get-scene-events (:scene db)]})

(defn get-scenes [{:keys [db]} _]
  (merge
   (service/get-scenes :get-scenes-succcess :get-scenes-failure)
   {:db (assoc db :loading true)}))

(defn get-scenes-succcess [db [_ r]]
  (assoc db :loading false :scenes r))

(defn get-scenes-failure [db [_ r]]
  (assoc db :error r))

(defn reg-events []
  (do
    (re-frame/reg-event-db :initialize initialize-event)

    (re-frame/reg-event-fx :get-scenes get-scenes)
    (re-frame/reg-event-db :get-scenes-succcess get-scenes-succcess)
    (re-frame/reg-event-db :get-scenes-failure get-scenes-failure)

    (re-frame/reg-event-fx :get-scene-events get-scene-events)
    (re-frame/reg-event-db :get-scene-events-succcess get-scene-events-succcess)

    (re-frame/reg-event-fx :remove-scene-event remove-scene-event)
    (re-frame/reg-event-fx :add-scene-event add-scene-event)
    (re-frame/reg-event-fx :add-scene-event-succcess add-scene-event-succcess)

    (re-frame/reg-event-fx :nav-scenes nav-scenes)
    (re-frame/reg-event-fx :nav-scene-events nav-scene-events)

    ))


