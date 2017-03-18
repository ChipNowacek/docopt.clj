(defproject org.clojars.carocad/docopt "0.1.1"
  :description "Docopt creates beautiful command-line interfaces - clojure (unofficial) version"
  :url "https://github.com/carocad/docopt.clj"
  :license {:name "MIT"
            :url "https://github.com/carocad/docopt.clj/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [instaparse "1.4.5"]
                 [org.clojure/data.json "0.2.6"]]
  :profiles {:test {:dependencies [[org.clojure/data.json "0.2.6"]]}})
