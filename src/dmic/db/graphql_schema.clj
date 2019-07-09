(ns dmic.db.graphql-schema
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.schema :refer [compile]]
    [com.walmartlabs.lacinia.util :as util]
    [dmic.db.query :as q]))

(def schema
  '{:enums
    {:node_type
     {:description "Node type"
      :values [:provincia :departamento :localidad]}}

    :objects
    {:node
     {:fields {:id {:type ID}
               :name {:type String}
               :type {:type :node_type}
               :custom_id {:type String}
               :parent {:type :node}
               ;:resolve :query/node-by-id}
               :ancestors {:type (list :node)}}}}
    ;:resolve :query/node-by-id}}}}

    :queries
    {:node_by_id {:type (non-null :node)
                  :description "Find a node by its ID"
                  :args {:id {:type (non-null String)}}
                  :resolve :query/node-by-id}}})

(defn node-by-id [db]
  (fn [_ {id :id} _]
    (q/find-node-by-id id)))

(defn compile-schema [db]
  (-> schema
    (util/attach-resolvers {:query/node-by-id (node-by-id db)})
    compile))
