(ns dmic.db.graphql
  (:require
    [com.walmartlabs.lacinia.schema :as schema]
    [com.walmartlabs.lacinia.util :as util]
    [dmic.db.query :as q]))

(def graphql-schema
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
               :ancestors {:type (list :node)
                           :resolve :query/resolve-ancestors}}}}

    :queries
    {:node_by_id {:type (non-null :node)
                  :description "Find a node by its ID"
                  :args {:id {:type (non-null String)}}
                  :resolve :query/node-by-id}}})

(defn add-string-id [{id :db/id :as entity}]
  (cond-> entity
    id (assoc :id (str id))))

(defn node-by-id [db]
  (fn [_ {id :id} _]
    (let [node (q/find-node-by-id id)]
      (add-string-id node))))

(defn resolve-ancestors [db]
  (fn [_ _ {id :db/id}]
    (let [nodes (q/find-node-ancestors id)]
      (map add-string-id nodes))))

(defn compile-schema [db]
  (-> graphql-schema
    (util/attach-resolvers
      {:query/node-by-id (node-by-id db)
       :query/resolve-ancestors (resolve-ancestors db)})
    schema/compile))
