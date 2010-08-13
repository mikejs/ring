(defproject ring/ring-mongrel2-adapter "0.2.5-SNAPSHOT"
  :description "Ring Mongrel2 adapter."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [ring/ring-core "0.2.5"]
                 [ring/ring-servlet "0.2.5"]]
  :native-dependencies [[org.clojars.mikejs/jzmq-native-deps
                         "2.0.7-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
                     [native-deps "1.0.1"]])