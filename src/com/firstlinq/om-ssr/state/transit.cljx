(ns com.firstlinq.om-ssr.state.transit
  (:require [cognitect.transit :as t])
  #+clj (:import (java.io ByteArrayOutputStream)))

#+clj
(defn serialise [data]
  (if-not data
    (byte-array 0)
    (let [out (ByteArrayOutputStream.)
          writer (t/writer out :json)]
      (t/write writer data)
      (.toByteArray out))))


#+clj
(defn deserialise [stream]
  (when stream
    (t/read (t/reader stream :json))))


#+cljs
(defn deserialise [string]
  (when string
    (t/read (t/reader :json) string)))