(ns dmic.db.schema)

(def node-schema
  [{:db/ident :node/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Node name"}

   {:db/ident :node/type
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc "Node type"}

   {:db/ident :node/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Custom ID for a node"}

   {:db/ident :node/parent
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Node parent"}

   {:db/ident :node/ancestors
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    ;:db/isComponent true
    :db/doc "Node ancestors"}

   {:db/ident :node/owners
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "Node owners"}])
