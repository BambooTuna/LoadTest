cd tools/gatling-runner/target/gatling
rm -rf ./bidsimulation
mkdir bidsimulation
gsutil cp -r bidsimulation gs://gatling-1055/gatling_log/bidsimulation/
cd ../../../../
pwd
sbt gatling-runner/gatling:generateReport
