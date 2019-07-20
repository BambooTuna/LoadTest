FROM hseeberger/scala-sbt:11.0.2_2.12.8_1.2.8

ARG project_dir=/application
WORKDIR $project_dir
COPY ./ $project_dir

CMD [ "sbt", "run/boot" ]