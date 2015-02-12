(ns com.firstlinq.om-ssr.router
  #+cljs (:require [om.dom :as dom :include-macros true]))

#+cljs
(defprotocol Router
  (navigate-to [this path])
  (path-exists? [this path])
  (path-for [this key params]))

#+cljs
(defn- handle-navigate [router href]
  (fn [e]
    (when (path-exists? router href)
      (.preventDefault e)
      (navigate-to router href))
    nil))


#+clj
(defmacro link
  "Creates an anchor element with the link on-click handler defined using a
  custom navigator. Passed-in props must include a href property for it to work.
  Alternatively, specify route as [id params]"
  [router props & children]
  `(let [[fr# sr#] (:route ~props)
         href# (if (:href ~props)
                 (:href ~props)
                 (when (and fr# sr#)
                   (path-for ~router fr# sr#)))
         handler# (handle-navigate ~router href#)
         new-props# (into {:href     href#
                           :on-click handler#} ~props)]
     (dom/a new-props# ~@children)))
