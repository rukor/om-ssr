(ns com.firstlinq.om-ssr.render
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as log])
  (:import [javax.script Invocable ScriptEngineManager]))

(defn bootstrap-dev
  "Bootsrap a dev-mode nashorn environment."
  [nashorn-env goog-path]
  (doto nashorn-env
    ;; load goog base
    (.eval (-> (str goog-path "/base.js")
               io/resource
               io/reader))
    ;; load goog deps (required to support deps not in cljsdeps)
    (.eval (-> (str goog-path "/deps.js")
               io/resource
               io/reader))
    ;; load directly specified dependendencies
    (.eval (-> (str goog-path "/../cljs_deps.js")
               io/resource
               io/reader))
    ;; set goog to import javascript using nashorn-env load(path)
    (.eval (str "
      goog.global.CLOSURE_IMPORT_SCRIPT = function(path) {
          print(\"loading: resources/" goog-path "/\" + path);
          load(\"resources/" goog-path "/\" + path);
          return true;
        };"))
    (.eval "
      for(var path in goog.dependencies_.requires) {
        if (goog.dependencies_.requires[path][\"cljs.core\"]) {
          for (var namespace in goog.dependencies_.pathToNames[path])
          {
            goog.require(namespace);
          }
        }
      }
    ")))

(defn bootstrap-prod
  "Bootstrap a prod-mode nashorn environment. Here there is only one
  compiled file to load."
  [nashorn-env build]
  (doto nashorn-env
    (.eval (-> build
               io/resource
               io/reader))))

(defn- render-fn* [render-ns-name render-fn-name is-dev?]
  (log/info "Bootstrapping new instance: " render-ns-name, "; dev=" is-dev?)
  (try
    (let [js (cond->
               (doto (.getEngineByName (ScriptEngineManager.) "nashorn")
                 ; React requires either "window" or "global" to be defined.
                 ; Figwheel expects location.host to be defined (there is a defonce
                 ; somewhere that gets executed on load)
                 (.eval "var global = this, window=this, location={host:{}}"))

               (true? is-dev?)
               (bootstrap-dev "public/js/out/goog")

               (false? is-dev?)
               (bootstrap-prod "public/js/app.js"))
          render-ns (.eval js render-ns-name)]
      (log/info "Finished bootstrapping " render-ns-name, ": dev=" is-dev?)
      (fn [state]
        (try
          (.invokeMethod
            ^Invocable js
            render-ns
            render-fn-name
            (-> state list object-array))
          (catch Exception e
            (log/warn "Could not perform server-side rendering: " e)))))
    (catch Exception e
      (log/warn "Failed to bootstrap server-side rendering " e)
      (constantly nil))))


(defn create-render-fn
  "Returns a function to render fully-formed HTML.
  (fn render [app-state])

  If is-dev? is true, then return a function that bootstraps a new nashorn environment
  on each call so that we can pick up changed files, otherwise return a pre-bootstrapped
  function."
  [render-ns render-fn-name & {:keys [is-dev?]
                               :or   {is-dev? true}}]
  (if is-dev?
    #((render-fn* render-ns render-fn-name is-dev?) %)
    (render-fn* render-ns render-fn-name is-dev?)))
