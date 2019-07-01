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
  (-> type (s/replace " " "-") keyword))

(prn
  "Initializing Datofu"
  (d/transact conn {:tx-data (array/schema-tx)}))

(prn
  "Creating schema"
  (d/transact conn {:tx-data schema/node-schema}))

;(doseq [[n type] (take 1 (map-indexed vector types))]


(defn make-node [ancestors-map {:keys [name type parent]}]
  (let [parent-id (-> parent :name ancestors-map)]
    (cond-> {:node/name name
             :node/type (type->keyword type)}
      parent-id (assoc :node/parent parent-id
                       :node/ancestors [{:datofu.coll.array.cell/index 0
                                         :datofu.coll.array.cell/ref parent-id}]))))

(defn by-type [type]
  (let [reader (clojure.java.io/reader "data.json")]
    (->> (cheshire/parse-stream reader true)
      (filter (comp #{type} :type)))))

(defn find-by-type [type]
  (let [q (d/q '[:find ?e ?n
                 :in $ ?type
                 :where [?e :node/type ?type]
                 [?e :node/name ?n]]
            (d/db conn)
            (type->keyword type))]
    (->> q
      (map (comp vec reverse))
      (into {}))))

(defn migrate-node-by-type [[type & rest] ancestors-map]
  (prn
    (str "Seeding type " type)
    (->>
      (by-type type)
      (map (partial make-node ancestors-map))
      (hash-map :tx-data)
      (d/transact conn)))
  (when (seq rest)
    (migrate-node-by-type
      rest
      (merge ancestors-map (find-by-type type)))))

;(->> (find-by-type "provincia")
;  prn)

;(let [q (d/q '[:find ?e ?n (sum ?a)
;               :where [?e :node/type]
;                      [?e :node/name ?n]
;                      (or-join [?e ?a]
;                        (and
;                          [(ground 0) ?a])
;                        (and
;                          [?e :node/ancestors ?ancestor]
;                          [(ground 1) ?a]))]
;          (d/db conn))]
;  (->> q
;    (map (fn [[_ _ n]] n))
;    (frequencies)
;    (prn)))
;(migrate-node-by-type types {})
;(d/transact conn {:tx-data [{:db/excise 17592186046801}]})