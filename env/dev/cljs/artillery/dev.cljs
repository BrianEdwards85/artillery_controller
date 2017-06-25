(ns ^:figwheel-no-load artillery.dev
  (:require [artillery.core :as core]
            [re-frisk.core :as re-frisk]))

(enable-console-print!)
(re-frisk/enable-re-frisk!)
(core/init!)
