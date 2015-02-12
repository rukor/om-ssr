(ns com.firstlinq.om-ssr.state.silk
  (:require [domkm.silk :as silk]
            [clojure.string :as str]
            [com.firstlinq.om-ssr.state :refer [get-state]]))

(defn request->url
  "Original request->url in silk merges in req, which I found weird."
  [{:keys [scheme server-name server-port uri query-string]}]
  (silk/url {:scheme (name scheme)
             :host   (-> server-name (str/split #"\.") reverse vec)
             :port   (str server-port)
             :path   (silk/decode-path uri)
             :query  (silk/decode-query query-string)}))

(defn create-request->state
  "Creates a request->state function based on silk routes"
  [silk-routes & {:keys [state-fn]
                  :or   {state-fn get-state}}]
  (let [routes (silk/routes silk-routes)]
    (fn [request]
      (when-let [params (silk/match routes (request->url request))]
        (state-fn (::silk/name params) (into (:params request)
                                             (dissoc params ::silk/routes)))))))