(defproject com.firstlinq/om-ssr "0.1.0-SNAPSHOT"
            :description "Server Side Rendering for OM-cljs"
            :url "http://github.com/rukor/om-ssr"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-2816" :scope "provided"]
                           [org.omcljs/om "0.8.8" :scope "provided"]
                           [com.cognitect/transit-clj "0.8.259"]
                           [com.cognitect/transit-cljs "0.8.205"]]
            :global-vars {*warn-on-reflection* true}
            :profiles {:dev {:dependencies [[com.domkm/silk "0.0.4"]]}})
