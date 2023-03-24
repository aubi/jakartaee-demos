FROM payara/server-full:5.2022.5-jdk11

USER payara
ENV PAYARA_ARGS --debug

COPY target/*.war $DEPLOY_DIR
