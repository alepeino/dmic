(ns dmic.db.schema
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.schema :as schema]
    [com.walmartlabs.lacinia.util :as util]
    [dmic.db.query :as q]))

(defn node-by-id [db]
  (fn [_ {id :id} _]
    (q/find-node-by-id id)))

(defn graphql-schema [db]
  (-> (io/file "src/dmic/db/graphql-schema.edn")
    slurp
    edn/read-string
    (util/attach-resolvers {:query/node-by-id (node-by-id db)})
    schema/compile))
