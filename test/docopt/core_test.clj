(ns docopt.core-test
  (:require
    ;[clojure.data.json :as json]
    [instaparse.core :as insta]
    [clojure.string :as string]
    [docopt.core :as docopt]
    [clojure.test :refer [is]]
    [clojure.pprint :refer [pprint]]
    [docopt.unit-tests :refer [test-cases]]))

(def ^:private test-section-regex #"(?s)r\"{3}(.*?)(?:\n{3})")
(def ^:private docstring-regex #"(?s)(?:r\"{3})(.*?)(?:\"{3})")
(def ^:private input-regex #"(?s)(?:\$ )(.*?)\n")
(def ^:private return-regex #"(?:\$.*\n)(\{.*\}|\"user-error\")")

(def ^:private test-file-url "https://raw.github.com/docopt/docopt/511d1c57b59cd2ed663a9f9e181b5160ce97e728/testcases.docopt")
(def ^:private test-file (slurp test-file-url))

(defn print-test-fodder []
  pprint test-cases)

(defn- make-inputs
  [section]
  (let [full-str (map second (re-seq input-regex section))
        raw-arguments (map #(string/split % #"\s*(\s+)") full-str)]
    (map rest raw-arguments)))

(defn- make-returns
  [section]
  (map second (re-seq return-regex section)))

(defn- user-error?
  [return-value]
  (= "user-error" return-value))

; TODO: print a test number or similar and if the test was successfull
(defn- docopt-works?
  [docstring input return]
  (if (user-error? return)
    (is (= docstring (docopt/docopt docstring input)))
    (is (= return (docopt/docopt docstring input)))))

(defn- test-section
  [section]
  (let [docstring (first (map second (re-seq docstring-regex section)))
        input-collection (make-inputs section)
        return-collection (make-returns section)]
    (map docopt-works? (repeat docstring)
         (map seq input-collection)
         return-collection)))

(defn all-tests
  []
  (let [coll-sections (map first (re-seq test-section-regex test-cases))]
    (map test-section coll-sections)))

;(all-tests)

(def foo
  "Usage:
    prog.py [--count] --OUT --FILE...

    Options:
     -f <path> --FILE=<path>  input file[default:foo]
     -o --OUT                 out directory
     --count N                number of operations")

(def bar "
  Usage:
    quick_example.py tcp <host> <port> [--timeout]
    quick_example.py serial <port> [--baud] [--timeout]
    quick_example.py -h | --help | --version

  Options:
     --timeout=<value>  input file[default:1200]
     --baud DB          out directory
     -h --help          number of operations
     --version          version")

(docopt/parse bar ["-h"])
(docopt/parse bar ["tcp" "localhost" "20" "--timeout:3200"])
(docopt/parse bar ["serial" "20" "--baud:4" "--timeout:3200"])
(docopt/parse bar ["serial" "--baud:4" "--timeout=3200"])   ;why does this work?
(docopt/parse bar ["serial" "--baud:4" "--timeout 3200"])   ;nope
(docopt/parse bar ["serial" "--baud:4" "--timeout:3200"])
(docopt/parse bar ["cereal" "--baud:4" "--timeout:3200"])

(def bub ;this is the first test in the unit test set
  "
  usage:
       prog [-a -r -m <msg>]

    options:
     -a        Add
     -r        Remote
     -m <msg>  Message")

(docopt/parse bub ["-a" "-r" "-m=yourass"]) ;nope

;(docopt/parse "usage:
;       prog [-a -r -m <msg>]
;
;    options:
;     -a        Add
;     -r        Remote
;     -m <msg>  Message
;
;    " ["prog" "-a" "-r" "-m=yourass"])

;(docopt-works? "usage:
;       prog [-a -r -m <msg>]
;
;    options:
;     -a        Add
;     -r        Remote
;     -m <msg>  Message
;
;    ",
;  "prog -a -r -m=yourass", {"-a" true "-r" true "-m" "yourass"})

;(def baz (merge (docopt/non-terminal (first (insta/parse docopt/docopt-parser bar)))
;                (docopt/usage-rules (first (insta/parse docopt/docopt-parser bar)))
;                (docopt/options-rules (second (insta/parse docopt/docopt-parser bar)))))


