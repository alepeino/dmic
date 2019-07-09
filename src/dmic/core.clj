(ns dmic.core
  (:require
    [cheshire.core :as cheshire]
    [compojure.core :refer [defroutes ANY]]
    [compojure.route :as route]
    [com.walmartlabs.lacinia :refer [execute]]
    [dmic.db.schema :refer [graphql-schema]]
    [dotenv :refer [env]]
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.util.response :refer [redirect]]))

(def compiled-schema (graphql-schema {}))

(defn- content-type [request]
  (let [header (get-in request [:headers "content-type"])
        [_ type] (re-matches #"\s*(([^/]+)/([^ ;]+))\s*(\s*;.*)?" (str header))]
    (when type
      (keyword type))))

(defn extract-query [request]
  (let [query (case (content-type request)
                :application/json (-> request :body slurp cheshire/parse-string)
                (:params request))]
    (map (partial get query) ["query" "variables" "operationName"])))

(def graphql-handler
  (-> (fn [request]
        (let [[query vars] (extract-query request)]
          (if-not query
            (redirect "graphql/index.html")
            (let [result (execute compiled-schema query vars nil)]
              {:status 200
               :headers {"Content-Type" "application/json"}
               :body (cheshire/generate-string result)}))))
    (wrap-cors
      :access-control-allow-origin [#".*"]
      :access-control-allow-methods [:get :post])))

(defroutes routes
  (ANY "/graphql" [] graphql-handler)
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def handler
  (-> routes
    wrap-params))

(defn -main []
  (run-server handler {:port (Integer. (env :APP_PORT))}))
