FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8

ENV \
ENABLE_REMOTE_JMX_OPTS=true \
JVM_HEAP_MIN=1024M \
JVM_HEAP_MAX=1280M \
JVM_META_MAX=512M \
JMX_PORT=8999 \
DATADOG_HOSTNAME=datadog \
DATADOG_PORT=8125

ARG project_dir=/application
WORKDIR $project_dir
COPY ./ $project_dir

EXPOSE 80
EXPOSE 8999

CMD [ "sbt", "boot/run" ]