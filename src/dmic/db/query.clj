(ns dmic.db.query
  (:require
    [datomic.client.api :as d]
    [dmic.db.connection :refer [conn]]))

(defn find-node-by-id [id]
  (let [node (d/pull
               (d/db conn)
               [[:db/id :as :id]
                [:node/name :as :name]
                [:node/type :as :type]]
               (Long. id))]
    node))

(defn find-node-ancestors [id]
  (let [nodes (d/q '[:find ?i (pull ?id [[:db/id :as :id]
                                         [:node/name :as :name]
                                         [:node/type :as :type]])
                     :in $ ?e
                     :where
                            [?e :node/ancestors ?a]
                            [?a :datofu.coll.array.cell/index ?i]
                            [?a :datofu.coll.array.cell/ref ?id]]
                (d/db conn)
                id)]
    (->> nodes
      (sort-by first)
      (map second))))
