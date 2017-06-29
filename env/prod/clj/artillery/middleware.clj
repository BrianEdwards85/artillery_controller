(ns artillery.middleware
  (:require
   [ring.middleware.cookies :refer [wrap-cookies]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults (dissoc site-defaults :security))
      wrap-cookies
      )
 ;; (wrap-defaults handler site-defaults)
  )
