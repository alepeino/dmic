(ns dmic.dev
  (:require
    [dmic.core :refer [handler]]
    [dotenv :refer [env]]
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.reload :refer [wrap-reload]]
    [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn -main []
  (prn "Running Ring on dev mode")
  (run-server
    (-> #'handler wrap-reload wrap-stacktrace)
    {:port (Integer. (env :APP_PORT))}))
