(ns dmic.db.migrations
  (:require
    [cheshire.core :as cheshire]
    [datofu.coll.array :as array]
    [datofu.schema.dsl :as s]
    [datomic.client.api :as d]
    [dmic.db.connection :refer [conn]]))

(def node-schema
  [(s/attr :node/name :string "Node name")
   (s/attr :node/type :keyword "Node type")
   (s/attr :node/id :string "Custom ID for a node")
   (s/to-one :node/parent "Node parent")
   (s/to-many :node/ancestors :component "Node ancestors")
   (s/to-many :node/owners "Node ancestors")])

(defn type->keyword [type]
  (-> (name type) (clojure.string/replace " " "-") keyword))

(defn make-key [names]
  (apply str (interpose "-" names)))

(defn make-node [ancestors-map {:keys [name type ancestors]}]
  (let [ancestor-names (for [n (range (count ancestors))]
                         (->> (take (inc n) ancestors)
                           (map :name)
                           make-key))
        temp-id (make-key (concat ancestor-names [name]))
        ancestor-ids (keep ancestors-map ancestor-names)
        parent-id (last ancestor-ids)]
    (cond-> {:db/id temp-id
             :node/name name
             :node/type (type->keyword type)}
      parent-id
      (assoc :node/parent parent-id)

      (seq ancestor-ids)
      (assoc :node/ancestors (map-indexed
                               #(hash-map
                                  :datofu.coll.array.cell/index %1
                                  :datofu.coll.array.cell/ref %2)
                               ancestor-ids)))))

(defn read-nodes-by-type [type]
  (let [reader (clojure.java.io/reader "data.json")]
    (->> (cheshire/parse-stream reader true)
      (filter (comp #{type} :type)))))

(defn migrate-node-by-type [[type & rest] ancestors-map]
  (prn "Seeding type" type)
  (let [ids (->> (read-nodes-by-type type)
              (map (partial make-node ancestors-map))
              (hash-map :tx-data)
              (d/transact conn)
              :tempids)]
    (when (seq rest)
      (migrate-node-by-type rest (merge ancestors-map ids)))))

(prn
  "Initializing Datofu"
  (d/transact conn {:tx-data (array/schema-tx)}))

(prn
  "Creating schema"
  (d/transact conn {:tx-data node-schema}))

(migrate-node-by-type ["provincia" "departamento" "localidad"] {})
