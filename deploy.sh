cat ./infrastructure/staging/gcp/terraform/account.json | docker login -u _json_key --password-stdin https://gcr.io

. infrastructure/staging/gcp/gcloud-env

export CIRCLE_SHA1=100
export APP_IMAGE=cyberagenthack/adtech-compe-2019-d-loadtest
export IMAGE_NAME=gcr.io/${GOOGLE_PROJECT_ID}/${APP_IMAGE}
sbt docker:publishLocal
export LOCAL_CREDENTIAL_PATH=./infrastructure/staging/gcp/terraform/account.json
sbt gatling-runner/docker:publishLocal
docker tag ${APP_IMAGE}:latest ${IMAGE_NAME}:${CIRCLE_SHA1}
docker push ${IMAGE_NAME}

docker tag ${GATLING_IMAGE}:latest ${GATLING_IMAGE_NAME}:${CIRCLE_SHA1}
docker push ${GATLING_IMAGE_NAME}

curl https://storage.googleapis.com/kubernetes-helm/helm-v2.12.3-linux-amd64.tar.gz | tar zx linux-amd64/helm
mv linux-amd64/helm /usr/local/bin/helm; rm -rf linux-amd64

gcloud auth activate-service-account --key-file ./infrastructure/staging/gcp/terraform/account.json
gcloud --quiet config set project $GOOGLE_PROJECT_ID
gcloud --quiet config set compute/region $GOOGLE_COMPUTE_REGION
gcloud --quiet config set compute/zone $GOOGLE_COMPUTE_ZONE
gcloud --quiet container clusters get-credentials $GOOGLE_CLUSTER_NAME

cd ./infrastructure/staging/gcp/k8s
sh ./setup-k8s.sh