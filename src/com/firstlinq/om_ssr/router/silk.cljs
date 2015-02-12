(ns com.firstlinq.om-ssr.router.silk
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [com.firstlinq.om-ssr.router :refer [Router] :as r]
            [com.firstlinq.om-ssr.state :refer [get-state]]
            [domkm.silk :as silk]
            [cljs.core.async :refer [put! chan <!]]
            [goog.events :as events])
  (:import [goog.history Html5History EventType]))

(defrecord SilkRouter [routes ch handler]
  Router
  (navigate-to [_ path]
    (when ch (put! ch path)))

  (path-exists? [_ path]
    (some? (silk/arrive routes path)))

  (path-for [_ key params]
    (silk/depart routes key params)))

(defn- create-history-channel [routes handler]
  (when (.isSupported Html5History)
    (let [ch (chan)
          history (doto (Html5History.)
                    (.setUseFragment false)
                    (.setPathPrefix "")
                    (.setEnabled true))]

      (events/listen
        history
        EventType/NAVIGATE
        (fn [event]
          (when (.-isNavigation event)
            (put! ch (.-token event)))
          nil))
      (go
        (while true
          (let [href (<! ch)]
            (when-let [route-map (silk/arrive routes href)]
              (when handler
                (. history (setToken href nil))
                (handler (get route-map ::silk/name) route-map))))))
      ch)))

(defn silk-router
  "Creates a silk based router"
  [routes & [handler]]
  (let [routes (silk/routes routes)
        handler (or handler get-state)
        history-ch (create-history-channel routes handler)]
    (map->SilkRouter {:routes  routes
                      :handler handler
                      :ch      history-ch})))