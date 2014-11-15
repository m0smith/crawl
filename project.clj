(defproject crawl "0.2.1-SNAPSHOT"
  :description "
# CRAWL
A dungeon crawler

******
## Links

### Marginalia

[Marginalia](http://gdeer81.github.io/marginalia)
[Markdown](http://daringfireball.net/projects/markdown/syntax)
[MathJax](http://www.mathjax.org/)     

### test.check
[test.check](https://github.com/clojure/test.check)

### monads, applicaton context and dependecy injection
[5 Faces of Dependency Injection in Clojure](http://software-ninja-ninja.blogspot.com/2014/04/5-faces-of-dependency-injection-in.html)

"
  :main crawl.core
  :url "https://github.com/m0smith/crawl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [fx-clj "0.1.0"]
                 [org.clojure/algo.monads "0.1.5"]
                 [com.matthiasnehlsen/inspect "0.1.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  
  :profiles { :dev 
             { :dependencies [
                              [marginalia "0.8.0"]
                              
                              [org.clojure/test.check "0.5.9"] ;; property testing
                              ]}})

