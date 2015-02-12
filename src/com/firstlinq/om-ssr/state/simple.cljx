(ns com.firstlinq.om-ssr.state.simple)

(defmulti get-state (fn [route-id route-params] route-id))

(defmethod get-state :default [route-id route-params]
  {:route-id route-id
   :params   route-params})