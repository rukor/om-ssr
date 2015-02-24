(ns com.firstlinq.om-ssr.state)

(defmulti get-state (fn [state route-id route-params opts] route-id))

#+clj
(defmethod get-state :default [state route-id route-params opts]
  {:route {:id     route-id
           :params route-params}})

#+cljs
(defmethod get-state :default [state route-id route-params opts]
  (when-let [alert (.-alert js/window)]
    (alert (str "Install a handler for navigating to " route-id))))
