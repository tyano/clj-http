(ns clj-http.test.support
  (:use [name.stadig.conjecture])
  (:require [cheshire.core :as json]
            [ring.adapter.jetty :as ring]))

(defn handler [req]
  ;;(pp/pprint req)
  ;;(println) (println)
  (condp = [(:request-method req) (:uri req)]
    [:get "/get"]
    {:status 200 :body "get"}
    [:get "/clojure"]
    {:status 200 :body "{:foo \"bar\" :baz 7M :eggplant {:quux #{1 2 3}}}"
     :headers {"content-type" "application/clojure"}}
    [:get "/clojure-bad"]
    {:status 200 :body "{:foo \"bar\" :baz #=(+ 1 1)}"
     :headers {"content-type" "application/clojure"}}
    [:get "/json"]
    {:status 200 :body "{\"foo\":\"bar\"}"}
    [:get "/json-bad"]
    {:status 400 :body "{\"foo\":\"bar\"}"}
    [:get "/redirect"]
    {:status 302 :headers
     {"location" "http://localhost:18080/redirect"}}
    [:get "/redirect-to-get"]
    {:status 302 :headers
     {"location" "http://localhost:18080/get"}}
    [:head "/head"]
    {:status 200}
    [:get "/content-type"]
    {:status 200 :body (:content-type req)}
    [:get "/header"]
    {:status 200 :body (get-in req [:headers "x-my-header"])}
    [:post "/post"]
    {:status 200 :body (slurp (:body req))}
    [:get "/error"]
    {:status 500 :body "o noes"}
    [:get "/timeout"]
    (do
      (Thread/sleep 10)
      {:status 200 :body "timeout"})
    [:delete "/delete-with-body"]
    {:status 200 :body "delete-with-body"}
    [:post "/multipart"]
    {:status 200 :body (:body req)}
    [:get "/get-with-body"]
    {:status 200 :body (:body req)}
    [:options "/options"]
    {:status 200 :body "options"}
    [:copy "/copy"]
    {:status 200 :body "copy"}
    [:move "/move"]
    {:status 200 :body "move"}
    [:patch "/patch"]
    {:status 200 :body "patch"}
    [:get "/headers"]
    {:status 200 :body (json/encode (:headers req))}))

(defn run-server
  []
  (defonce server
    (ring/run-jetty handler {:port 18080 :join? false})))

(singleton-fixture
 (def server-fixture
   (singleton-fixture
    (fn [f]
      (println "[+++] Starting Jetty test server")
      (let [server (ring/run-jetty handler {:port 18080 :join? false})]
        (when f (f))
        (println "[---] Stopping Jetty test server"))))))
