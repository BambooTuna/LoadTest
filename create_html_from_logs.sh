cd tools/gatling-runner/target/gatling
rm -rf ./bidsgsimulation-20190904204628929
mkdir bidsgsimulation-20190904204628929
gsutil cp -r gs://gatling-1055/gatling_log/bidsimulation/ ./
cd ../../../../
pwd
sbt gatling-runner/gatling:generateReport
