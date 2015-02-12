(ns com.firstlinq.om-ssr.router
  (:require [om.dom :as dom :include-macros true]))

(defprotocol Router
  (navigate-to [this path])
  (path-exists? [this path])
  (path-for [this key params]))

(defn link
  "Creates an anchor tag with the link on-click handler defined using a
  custom navigator. Passed-in props must include a href property."
  [router {:keys [href] :as props} & children]
  (apply dom/a (merge {:on-click (navigate-to router href)} props) children))