(ns artillery.subs
  (:require [re-frame.core :as re-frame]))

(defn reg-subs []

  (re-frame/reg-sub
   :page
   (fn [db _]
     (:page db)))

  (re-frame/reg-sub
   :scenes
   (fn [db _]
     (:scenes db)))

  (re-frame/reg-sub
   :scene
   (fn [db _]
     (let [scene-id (:scene db)
           scene (first (filter #(= scene-id (:id %)) (:scenes db)))]
       (if scene
         scene
         {:id scene-id
          :description (str "Scene-" scene-id)}
         ))))

  (re-frame/reg-sub
   :scene-events
   (fn [db _]
     (map #(assoc %1 :triggered (contains? (:triggered-events db) %1)) 
          (:scene_events db))))

  (re-frame/reg-sub
   :triggered-events
   (fn [db _]
     (:triggered-events db)))

  )

