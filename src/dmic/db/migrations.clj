(ns dmic.db.migrations
  (:require
    [cheshire.core :as cheshire]
    [clojure.string :as s]
    [datofu.coll.array :as array]
    [datomic.client.api :as d]
    [dmic.db.connection :refer [conn]]
    [dmic.db.schema :as schema]))

(def types ["provincia" "departamento"])

(defn type->keyword [type]
  (-> type name (s/replace " " "-") keyword))

(defn node-additions [ancestors-map {:keys [name type ancestors]}]
  (let [ancestor-names (for [n (range (count ancestors))]
                         (->> (take (inc n) ancestors)
                           (map :name)
                           (interpose "-")
                           (apply str)))
        temp-id (->> [name]
                  (concat ancestor-names)
                  (interpose "-")
                  (apply str))
        ancestor-ids (keep ancestors-map ancestor-names)]
        ;ancestor-id (prn parent-id ancestors-map)]))
    (concat [[:db/add temp-id :node/name name]
             [:db/add temp-id :node/type type]]
            (vec (map-indexed #(vector %1 %2) ancestor-ids)))))
    ;(cond->> [{:db/add :node/name name
    ;           :node/type (type->keyword type)]
    ;  parent-id (assoc :node/parent parent-id
    ;                   :node/ancestors [{:datofu.coll.array.cell/index 0
    ;                                     :datofu.coll.array.cell/ref parent-id)]))

(defn by-type [type]
  (let [reader (clojure.java.io/reader "data.json")]
    (->> (cheshire/parse-stream reader true)
      (filter (comp #{type} :type)))))

;(defn find-by-type [type]
;  (let [q (d/q '[:find ?e ?n
;                 :in $ ?type
;                 :where [?e :node/type ?type]
;                        [?e :node/name ?n]
;                        [?e :node/ancestors ?an-cel]]
;            (d/db conn)
;            (type->keyword type)
;            '[])]
;    (->> q
;      (map (comp vec reverse))
;      (into {}))))

(defn migrate-node-by-type [[type & rest] ancestors-map]
  (let [parsed (by-type type)]
    (->> parsed
      (map (partial node-additions ancestors-map))
      prn)))
    ;(prn
    ;  (str "Seeding type " type)
    ;  (->> parsed
    ;    (map (partial make-node ancestors-map))
    ;    (hash-map :tx-data)
    ;    (d/transact conn)]
    ;(->> parsed
    ;  (map :parent)
    ;  (prn "XXXXXX")
    ;(when (seq rest)
    ;  (migrate-node-by-type
    ;    rest
    ;    (merge ancestors-map (find-by-type type))]))

;(prn
;  "Initializing Datofu"
;  (d/transact conn {:tx-data (array/schema-tx)}))
;
;(prn
;  "Creating schema"
;  (d/transact conn {:tx-data schema/node-schema}))
;
(migrate-node-by-type (rest types) {})
