(ns artillery.core
    (:require [reagent.core :as reagent :refer [atom]]
              [artillery.handlers :as handlers]
              [artillery.subs :as subs]
              [artillery.views :as views]
              [re-frame.core :as re-frame]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views


(defn home-page []
  [:div [:h2 "Welcome to artillery"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About artillery"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (re-frame/dispatch [:nav-scenes]))

(secretary/defroute "/scene/:id" {:as params}
  (re-frame/dispatch [:nav-scene-events (:id params)]))

;; -------------------------
;; Initialize app

(def accountant-configuration
  {:nav-handler
   (fn [path]
     (secretary/dispatch! path))
   :path-exists?
   (fn [path]
     (secretary/locate-route path))})

(defn init! []
  (handlers/reg-events)
  (subs/reg-subs)
  (re-frame/dispatch-sync [:initialize])
  (accountant/configure-navigation! accountant-configuration)
  (accountant/dispatch-current!)
  (reagent/render [views/main-panel] (.getElementById js/document "app")))
 ;; (mount-root)
