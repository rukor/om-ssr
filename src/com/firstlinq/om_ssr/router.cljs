(ns com.firstlinq.om-ssr.router
  (:require [om-tools.dom :as dom :include-macros true]))

(defprotocol Router
  (navigate-to [this path])
  (path-exists? [this path])
  (path-for [this key params]))

(defn- handle-navigate [router href]
  (fn [e]
    (when (path-exists? router href)
      (.preventDefault e)
      (navigate-to router href))
    nil))

(defn link
  "Creates an anchor element with the link on-click handler defined using a
  custom navigator. Passed-in props must include a href property for it to work.
  Alternatively, specify route as [id params]"
  [router props & children]
  (let [[r-id r-params] (:route props)
        href (if (:href props)
               (:href props)
               (when r-id (path-for router r-id r-params)))
        handler (handle-navigate router href)
        new-props (into props {:href     href
                               :on-click handler})]
    (dom/a new-props children)))