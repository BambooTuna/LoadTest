sbt docker:publishLocal
INIT_MYSQL_SQL_PATH="`pwd`/tools/mysql/"
. ./tools/datadog/.env
sed -i -e 's!INIT_MYSQL_SQL_PATH!'${INIT_MYSQL_SQL_PATH}'!' ./infrastructure/staging/gcp/k8s/db-deployment.yml

kubectl apply -f "https://raw.githubusercontent.com/DataDog/datadog-agent/master/Dockerfiles/manifests/rbac/clusterrole.yaml"
kubectl apply -f "https://raw.githubusercontent.com/DataDog/datadog-agent/master/Dockerfiles/manifests/rbac/serviceaccount.yaml"
kubectl apply -f "https://raw.githubusercontent.com/DataDog/datadog-agent/master/Dockerfiles/manifests/rbac/clusterrolebinding.yaml"
kubectl create secret generic datadog-secret --from-literal api-key=${DD_API_KEY}
kubectl apply -f ./infrastructure/staging/gcp/k8s/datadog-deployment.yml

kubectl apply -f ./infrastructure/staging/gcp/k8s/db-secret.yml
kubectl apply -f ./infrastructure/staging/gcp/k8s/db-deployment.yml

kubectl apply -f ./infrastructure/staging/gcp/k8s/app-deployment-local.yml