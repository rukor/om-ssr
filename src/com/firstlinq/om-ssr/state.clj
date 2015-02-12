(ns com.firstlinq.om-ssr.state
  (:require [cognitect.transit :as t])
  (:import (java.io ByteArrayOutputStream)))

(defn serialise [data]
  (if-not data
    (byte-array 0)
    (let [out (ByteArrayOutputStream.)
          writer (t/writer out :json)]
      (t/write writer data)
      (.toByteArray out))))

(defn deserialise [stream]
  (when stream
    (t/read (t/reader stream :json))))

(defmulti get-state (fn [route-id request] route-id))

(defmethod get-state :default [route-id request] nil)

(defn route-state-handler
  [template renderer & {:keys [route-id-key params-key params-fn]
                        :or   {route-id-key :route-id
                               params-key   :params
                               params-fn    :params}}]
  (fn [route-id]
    (fn [request]
      (let [params (params-fn request)
            state (cond-> (or (get-state route-id request) {})
                          route-id-key (assoc route-id-key route-id)
                          params-key (assoc params-key params))
            state-string (String. (serialise state))
            rendered (renderer state-string)]
        (template state-string rendered)))))