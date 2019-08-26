. ../env
export DOCKER_TAG=`echo "${CIRCLE_PROJECT_USERNAME}/${CIRCLE_PROJECT_REPONAME}:latest" | tr '[:upper:]' '[:lower:]'`
sed -i -e 's!DOCKER_TAG!'${DOCKER_TAG}'!' ./app-deployment.yml

gcloud auth activate-service-account --key-file ${HOME}/account.json
gcloud --quiet config set project $GOOGLE_PROJECT_ID
gcloud --quiet config set compute/region $GOOGLE_COMPUTE_REGION
gcloud --quiet config set compute/zone $GOOGLE_COMPUTE_ZONE
gcloud --quiet container clusters get-credentials $GOOGLE_CLUSTER_NAME

kubectl apply -f ./db-secret.yml
kubectl apply -f ./db-deployment.yml
kubectl apply -f ./app-deployment.yml