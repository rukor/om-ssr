(ns com.firstlinq.om-ssr.state)

(defmulti get-state (fn [route-id route-params] route-id))

#+clj
(defmethod get-state :default [route-id route-params]
  {:route-id route-id
   :params   route-params})

#+cljs
(defmethod get-state :default [route-id route-params]
  (when-let [alert (.-alert js/window)]
    (alert (str "Install a handler for navigating to ") route-id)))
