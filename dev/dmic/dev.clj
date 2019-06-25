(ns dmic.dev
  (:require
    [dmic.core :refer [handler]]
    [dotenv :refer [env]]
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.reload :refer [wrap-reload]]))

(defn -main []
  (prn "Running Ring on dev mode")
  (run-server (wrap-reload #'handler) {:port (Integer. (env :APP_PORT))}))
