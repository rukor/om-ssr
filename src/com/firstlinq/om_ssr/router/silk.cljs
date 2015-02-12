(ns com.firstlinq.om-ssr.router.silk
  (:require [com.firstlinq.om-ssr.router :refer [Router]]
            [com.firstlinq.om-ssr.state :refer [get-state]]
            [domkm.silk :as silk]))

(defrecord SilkRouter [routes handler]
  Router
  (navigate-to [_ path]
    (when-let [route-map (silk/arrive routes path)]
      (let [route (get route-map ::silk/name)]
        (when (fn? handler)
          (handler route route-map)))))

  (path-exists? [_ path]
    (some? (silk/arrive routes path)))

  (path-for [_ key params]
    (silk/depart routes key params)))

(defn silk-router [routes & [handler]]
  (->SilkRouter (silk/routes routes) (or handler get-state)))