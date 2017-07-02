(ns artillery.views
  (:require [reagent.core  :as r]
            [artillery.service :as service]
            [re-frame.core :refer [subscribe dispatch]]))

(defn glyphicon [n]
      [:span {:class (str "glyphicon glyphicon-" n) }])

(defn scene-event-row [event add-handler]
  [:tr (if (:triggered event) {:class "success"})
   [:td (:device event)]
   [:td (:addr event)]
   [:td (:pin event)]
   [:td (:delay event)]
   [:td (:offset event)]
   [:td
    [:button {:class "btn btn-primary btn-sm" :type "button" :on-click #(add-handler (:idx event))}
     [glyphicon "plus"]]
    [:button {:class "btn btn-primary btn-sm" :type "button" :on-click #(dispatch [:remove-scene-event event])}
     [glyphicon "trash"]]
    [:button {:class "btn btn-primary btn-sm" :type "button" :on-click #(service/fire-event event)}
     [glyphicon "fire"]]  ]])



(defn scene-event-table [events add-handler]
  [:table {:class "table"}
   [:thead
    [:tr
     [:th "Device"]
     [:th "Address"]
     [:th "Pin"]
     [:th "Delay"]
     [:th "Offset"]
     [:th "Action"]]]
    [:tbody
     (for [event events]
       ^{:key (str "SCENE_EVENT (" (:scene event) ":" (:idx event) ")")} [scene-event-row event add-handler])]])


(defn modal [close content]
  [:div.add-modal
   [:div.content
    [:div.headder
     [:button {:class "btn btn-default"
               :type "button"
               :on-click close}
      [glyphicon "remove"]]]
    content]])

(defn bootstrap-input [i name a]
  [:div {:class "form-group"}
   [:label {:for i} name]
   [:input {:class "form-control"
            :id i
            :placeholder name
            :value @a
            :on-change #(reset! a (-> % .-target .-value))}]])

(defn scene-event-add-form
  [scene idx close]
   (let [device (r/atom "")
         addr (r/atom "")
         pin (r/atom "")
         delay (r/atom "")]
     (fn []
       [:form
        [bootstrap-input "deviceInput" "Device" device]
        [bootstrap-input "addrInput" "Address" addr]
        [bootstrap-input "pinInput" "Pin" pin]
        [bootstrap-input "delayInput" "Delay" delay]
        [:button {:class "btn btn-default"
                  :type "button"
                  :on-click #(do
                               (dispatch [:add-scene-event (->
                                                            {:device @device
                                                                  :addr @addr
                                                                  :pin @pin
                                                                  :delay @delay
                                                                  :scene scene}
                                                            (cond-> @idx (assoc :idx @idx)))])
                               (close))}
         "Add"]])))


(defn scene-event-panel []
  (let [scene (subscribe [:scene])
        events (subscribe [:scene-events])
        idx (r/atom nil)
        add-event (r/atom false)]
    (fn []
      [:dev
       [:h3 (str "Events: " (:description @scene))]
       [:div
        [:button {:class "btn btn-default" :type "button" :on-click #(reset! add-event true)} "Add"]
        [:button {:class "btn btn-default" :type "button" :on-click #(dispatch [:run-scene (:id @scene)])} "Run!"]]
       [scene-event-table @events #(do
                                     (reset! idx %)
                                     (reset! add-event true))]
       (if @add-event
         [modal #(reset! add-event false)
          [scene-event-add-form (:id @scene) idx #(do
                                                    (reset! idx nil)
                                                    (reset! add-event false))]])])))

(defn add-scene-add-form [close]
  (let [desc (r/atom "")]
    (fn []
      [:form
       [bootstrap-input "descInput" "Description" desc]
       [:button {:class "btn btn-default"
                 :type "button"
                 ::on-click #(do
                               (dispatch [:add-scene @desc])
                               (close))}
        "Add"]])))

(defn scene-item [scene]
  [:li [:a {:href (str "/scene/" (:id scene))} (:description scene)]])

(defn scenes-list [scenes]
  [:ul
   (for [scene scenes]
     ^{:key (str "scene-" (:id scene))} [scene-item scene])])

(defn scenes-panel []
  (let [scenes (subscribe [:scenes])
        add-scene (r/atom false)]
    (fn []
      [:div
       [:div "Scenes"]
       [:div
        [:button {:class "btn btn-default" :type "button" :on-click #(reset! add-scene true)} "Add"]]
       [scenes-list @scenes]
       (if @add-scene
         [modal #(reset! add-scene false)
          [add-scene-add-form #(reset! add-scene false)]])]
      )))

(defn main-panel []
  (let [page (subscribe [:page])]
    (fn []
      (println "P: " page)
      [:div [:h2 "Welcome to artillery"]
       (case @page
         :scenes [scenes-panel]
         :events [scene-event-panel]
         [:div "Unkown"])])))
