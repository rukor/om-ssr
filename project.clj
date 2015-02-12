(defproject com.firstlinq/om-ssr "0.1.0-SNAPSHOT"
            :description "Server Side Rendering for OM-cljs"
            :url "http://github.com/rukor/om-ssr"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :source-paths ["src" "target/src"]
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-2816" :scope "provided"]
                           [org.omcljs/om "0.8.8" :scope "provided"]
                           [com.taoensso/timbre "3.3.1"]
                           [com.cognitect/transit-clj "0.8.259"]
                           [com.cognitect/transit-cljs "0.8.205"]]
            :global-vars {*warn-on-reflection* true}
            :prep-tasks [["cljx" "once"] "javac" "compile"]
            :plugins      [[com.keminglabs/cljx "0.4.0" :exclusions [org.clojure/clojure]]]
            :cljx {:builds [{:source-paths ["src"]
                             :output-path  "target/src"
                             :rules        :clj}
                            {:source-paths ["src"]
                             :output-path  "target/src"
                             :rules        :cljs}]}
            :profiles {:dev {:dependencies [[com.domkm/silk "0.0.4"]]}})
