FROM payara/server-full:5.2022.5-jdk11

USER payara
ENV PAYARA_ARGS --debug

RUN echo "set-config-property --propertyName=processingThreadsCount --propertyValue=5 --source=domain" >> $POSTBOOT_COMMANDS
RUN echo "set-config-property --propertyName=net.aubrecht.mandelbrot.mandelbrotvideo.service.ProductServiceClient/mp-rest/url --propertyValue=http://picture:8080/MandelbrotPicture/rest/ --source=domain" >> $POSTBOOT_COMMANDS

RUN cat $POSTBOOT_COMMANDS

COPY target/*.war $DEPLOY_DIR
