(defproject com.firstlinq/om-ssr "0.1.0-SNAPSHOT"
            :description "Server Side Rendering for OM-cljs"
            :url "http://github.com/rukor/om-ssr"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :source-paths ["src" "target/src"]
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-2816" :scope "provided"]
                           [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                           [org.omcljs/om "0.8.8" :scope "provided"]
                           [prismatic/om-tools "0.3.10"]
                           [com.domkm/silk "0.0.5"]
                           [bidi "1.18.7"]
                           [com.taoensso/timbre "3.3.1"]
                           [com.cognitect/transit-clj "0.8.259"]
                           [com.cognitect/transit-cljs "0.8.205"]]
            :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]
            :global-vars {*warn-on-reflection* true}
            :prep-tasks [["cljx" "once"] "javac" "compile"]
            :plugins [[com.keminglabs/cljx "0.4.0" :exclusions [org.clojure/clojure]]]
            :cljx {:builds [{:source-paths ["src"]
                             :output-path  "target/src"
                             :rules        :clj}
                            {:source-paths ["src"]
                             :output-path  "target/src"
                             :rules        :cljs}]})
