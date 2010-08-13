(ns ring.adapter.mongrel2
  (:import (org.zeromq ZMQ))
  (:use clojure.contrib.json))

(defn- parse-netstring [s]
  (let [parts (.split s ":" 2)
        len (Integer/parseInt (first parts))]
    (apply str (take len (second parts)))))

(defn- make-netstring [s]
  (str (count s) ":" s ","))

(defn- parse-m2-request [req]
  (print req)
  (let [[uuid client-id uri rest] (.split req " " 4)
        rest (.split rest ":" 2)
        header-len (Integer/parseInt (first rest))
        [headers body] (split-at header-len (second rest))
        headers (read-json (apply str headers))
        body (parse-netstring body)]
    {:uuid uuid :client-id client-id :uri uri
     :headers headers :body body :request-method (headers "METHOD")
     :scheme :http :server-name "localhost" :server-port 6767
     :remote-addr "localhost" :content-length (count body)}))

(defn- make-http-reply [resp]
  (let [header-str (apply str (map #(str (first %) ": " (second %) "\r\n")
                                   (:headers resp)))]
    (str "HTTP/1.1 " (:status resp)
         " OK\r\nContent-Length: " (count (:body resp))
         "\r\n" header-str "\r\n" (:body resp))))

(defn run-mongrel2 [handler options]
  (let [ctx (ZMQ/context 1)
        sub (.socket ctx ZMQ/SUB)
        pub (.socket ctx ZMQ/PUB)]
    (.connect sub "tcp://127.0.0.1:5566")
    (.setsockopt sub ZMQ/SUBSCRIBE "")
    (.connect pub "tcp://127.0.0.1:5565")
    (while (not (.isInterrupted (Thread/currentThread)))
      (let [req (parse-m2-request (String. (.recv sub 0)))
            ids (make-netstring (:id req))
            resp (make-http-reply (handler req))
            reply (str (:uuid req) " " ids " " resp)]
        (.send pub (.getBytes reply "US-ASCII") 0)))))