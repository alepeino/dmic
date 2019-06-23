(ns dmic.db.connection
  (:require [datomic.client.api :as d]
            [dotenv :refer [env]]))

(def cfg {:server-type :peer-server
          :access-key  (env :ACCESS_KEY)
          :secret      (env :SECRET)
          :endpoint    (str (env :DB_HOST) ":" (env :DB_PORT))})

(def client (d/client cfg))

(def conn (d/connect client {:db-name (env :DB_NAME)}))
