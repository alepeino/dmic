(ns dmic.core
  (:require
    [compojure.core :refer [defroutes GET]]
    [compojure.route :as route]
    [dotenv :refer [env]]
    [org.httpkit.server :refer [run-server]]))

(defroutes handler
  (GET "/" [] "<h1>Hello World</h1>")
  (route/not-found "<h1>Page not found</h1>"))

(defn -main []
  (run-server handler {:port (Integer. (env :APP_PORT))}))
