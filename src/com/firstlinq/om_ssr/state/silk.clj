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
  [silk-routes & {:keys [state-fn user-fn user-key opts]
                  :or   {state-fn get-state
                         user-fn  :user
                         user-key :user}}]
  (let [routes (silk/routes silk-routes)]
    (fn [request]
      (when-let [match (silk/arrive routes (:uri request))]
        (let [params (assoc match :query (:query-params request))
              user   (user-fn request)
              init   {user-key user}
              state  (state-fn init                         ; initial state
                               (::silk/name params)         ; route id
                               (into (:params request)      ; route params
                                     (dissoc params ::silk/routes ::silk/pattern))
                               opts)]                       ; optional stuff
          state #_(merge init state))))))                   ; no longer merge the state, let the client do so
