FROM datomic-base

EXPOSE 8998

CMD bin/run -m datomic.peer-server -h ${DB_HOST} -p ${DB_PORT} -a ${ACCESS_KEY},${SECRET} -d ${DB_NAME},datomic:mem://${DB_NAME}
