FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8

ARG project_dir=/application
WORKDIR $project_dir
COPY ./ $project_dir

EXPOSE 80

CMD [ "sbt", "boot/run" ]