FROM openjdk:8u212-jre

ARG DATOMIC_ACCOUNT
ARG DOWNLOAD_KEY

ENV DATOMIC_VERSION 0.9.5786

RUN wget --http-user=${DATOMIC_ACCOUNT} --http-password=${DOWNLOAD_KEY} \
    https://my.datomic.com/repo/com/datomic/datomic-pro/${DATOMIC_VERSION}/datomic-pro-${DATOMIC_VERSION}.zip \
    -O /tmp/datomic.zip

RUN unzip /tmp/datomic.zip \
  && rm /tmp/datomic.zip \
  && mv /datomic-pro-${DATOMIC_VERSION} /datomic

WORKDIR /datomic

COPY config config
