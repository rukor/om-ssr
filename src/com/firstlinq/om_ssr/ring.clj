(ns com.firstlinq.om-ssr.ring
  (:require [com.firstlinq.om-ssr :refer [redirect-key]]))

(defn create-ring-handler
  "Creates a ring handler given the following arguments:

  request->state    takes a request map and returns a state map

  render-fn         a function that will take a app state and render an initial page

  state->string     a function that serialises the state to string, so it can be
                    embedded in the page for the client side to pick up. A corresponding
                    deserialise capability must be present in the client.

  template-fn       a template fn to pass on the route-id, rendered text, and serialised
                    state parameters"
  [request->state render-fn state->string template]
  (fn [req]
    (when-let [state (request->state req)]
      (if-let [location (redirect-key state)]
        ; redirect
        {:status  302
         :headers {"Location" location}
         :body    "Redirecting..."}

        ; else handle directly
        (let [serialised-state (state->string state)
              rendered (render-fn serialised-state)]
          (template state serialised-state rendered))))))