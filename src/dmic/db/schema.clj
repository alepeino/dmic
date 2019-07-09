(ns dmic.db.schema
  (:require [datofu.schema.dsl :as s]))

(def node-schema
  [(s/attr :node/name :string "Node name")
   (s/attr :node/type :keyword "Node type")
   (s/attr :node/id :string "Custom ID for a node")
   (s/to-one :node/parent "Node parent")
   (s/to-many :node/ancestors :component "Node ancestors")
   (s/to-many :node/owners "Node ancestors")])
