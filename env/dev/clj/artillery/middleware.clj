(ns artillery.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults (dissoc site-defaults :security))
      wrap-cookies
      wrap-exceptions
      wrap-reload))
