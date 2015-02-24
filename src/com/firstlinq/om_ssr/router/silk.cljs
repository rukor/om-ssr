(ns com.firstlinq.om-ssr.router.silk
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [com.firstlinq.om-ssr.router :refer [Router] :as r]
            [com.firstlinq.om-ssr.state :refer [get-state]]
            [domkm.silk :as silk]
            [clojure.string :as str]
            [cljs.core.async :refer [put! chan <!]]
            [goog.events :as events])
  (:import [goog.history Html5History EventType]))

(defrecord SilkRouter [routes ch]
  Router
  (navigate-to [_ path]
    (when ch (put! ch path)))

  (path-exists? [_ path]
    (some? (silk/arrive routes path)))

  (path-for [_ key params]
    (silk/depart routes key (or params {}))))

;--------------------
; inspiration from : https://github.com/steida/este-library/blob/master/este/history/tokentransformer.coffee
(defn- token-transformer [])

(set! (.-retrieveToken (.-prototype token-transformer))
      (fn [path-prefix location]
        (str (.substr (.-pathname location) (.-length path-prefix))
             (.-search location)
             (.-hash location))))

(set! (.-createUrl (.-prototype token-transformer))
      (fn [token path-prefix location]
        (str path-prefix token)))
;------------------

(defn- create-history-channel [routes handler]
  (when (.isSupported Html5History)
    (let [ch      (chan)
          history (doto (Html5History. nil (token-transformer.))
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
  (let [routes     (silk/routes routes)
        handler    (or handler get-state)
        history-ch (create-history-channel routes handler)]
    (map->SilkRouter {:routes routes
                      :ch     history-ch})))