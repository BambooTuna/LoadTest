FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8

ENV \
JVM_HEAP_MIN=512M \
JVM_HEAP_MAX=512M \
JVM_META_MAX=256M \
JMX_PORT=8999 \
DATADOG_HOSTNAME=datadog \
DATADOG_PORT=8125

ARG project_dir=/application
WORKDIR $project_dir
COPY ./ $project_dir

EXPOSE 80
EXPOSE 8999

CMD [ "sbt", "boot/run" ]