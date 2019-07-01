FROM datomic-base

ENV PEER_SERVER_HOST localhost
ENV PEER_SERVER_PORT 8998

ENV TRANSACTOR_HOST localhost
ENV TRANSACTOR_PORT 4334
ENV TRANSACTOR_PROTOCOL dev

EXPOSE ${PEER_SERVER_PORT}

CMD echo "(require '[datomic.api :as d]) (d/create-database \"datomic:${TRANSACTOR_PROTOCOL}://${TRANSACTOR_HOST}:${TRANSACTOR_PORT}/${DB_NAME}\")" | bin/repl \
    && bin/run -m datomic.peer-server -h ${PEER_SERVER_HOST} -p ${PEER_SERVER_PORT} -a ${ACCESS_KEY},${SECRET} -d ${DB_NAME},datomic:${TRANSACTOR_PROTOCOL}://${TRANSACTOR_HOST}:${TRANSACTOR_PORT}/${DB_NAME}
