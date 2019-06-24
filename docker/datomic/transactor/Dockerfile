FROM datomic-base

ENV TRANSACTOR_PROTOCOL dev
ENV TRANSACTOR_PORT 4334

EXPOSE ${TRANSACTOR_PORT}

CMD bin/transactor -Ddatomic.licenseKey=${LICENSE_KEY} config/${TRANSACTOR_PROTOCOL}-transactor.properties
