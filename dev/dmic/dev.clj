(ns dmic.dev
  (:require
    [dmic.core :refer [handler]]
    [dotenv :refer [env]]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.middleware.reload :refer [wrap-reload]]))

(defn -main []
  (prn "Running Ring on dev mode")
  (run-jetty (wrap-reload #'handler) {:port (Integer. (env :APP_PORT))}))
