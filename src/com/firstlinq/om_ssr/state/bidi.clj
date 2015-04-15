(ns com.firstlinq.om-ssr.state.bidi
  (:require [bidi.bidi :as bidi]
            [clojure.string :as str]
            [com.firstlinq.om-ssr.state :refer [get-state]]))

#_(defn request->url
  "Original request->url in silk merges in req, which I found weird."
  [{:keys [scheme server-name server-port uri query-string]}]
  (silk/url {:scheme (name scheme)
             :host   (-> server-name (str/split #"\.") reverse vec)
             :port   (str server-port)
             :path   (silk/decode-path uri)
             :query  (silk/decode-query query-string)}))

(defn create-request->state
  "Creates a request->state function based on silk routes"
  [routes & {:keys [state-fn user-fn user-key opts]
             :or   {state-fn get-state
                    user-fn  :user
                    user-key :user}}]
  (let [routes (bidi/compile-route routes)]
    (fn [request]
      (when-let [params (bidi/match-route routes (:uri request))]
        (let [user  (user-fn request)
              init  {user-key user}
              state (state-fn init                          ; initial state
                              (:handler params)             ; route id
                              (into (:params request)       ; route params
                                    params)
                              opts)]                        ; optional stuff
          state)))))                   ; no longer merge the state, let the client do so
