(ns com.firstlinq.om-ssr.router.bidi
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [com.firstlinq.om-ssr.router :refer [Router path-for] :as r]
            [com.firstlinq.om-ssr.state :refer [get-state]]
            [bidi.bidi :as b]
            [clojure.string :as str]
            [cljs.core.async :refer [put! chan <!]]
            [goog.events :as events])
  (:import [goog.history Html5History EventType]))

(defrecord BidiRouter [routes ch]
  Router
  (navigate-to [this path]
    (when ch
      (when-let [path (if (vector? path) (apply path-for this path) path)]
        (put! ch path))))

  (path-exists? [_ path]
    (some?
      (b/match-route routes path)))

  (path-for [_ key params]
    (apply b/path-for routes (apply concat (or params {})))))

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
            (when-let [route-map (b/match-route routes href)]
              (when handler
                (. history (setToken href nil))
                (handler (get route-map :handler) route-map))))))
      ch)))

(defn bidi-router
  "Creates a bidi based router"
  [routes & [handler]]
  (let [routes     (b/compile-route routes)
        handler    (or handler get-state)
        history-ch (create-history-channel routes handler)]
    (map->BidiRouter {:routes routes
                      :ch     history-ch})))