sbt docker:publishLocal
INIT_MYSQL_SQL_PATH="`pwd`/tools/mysql/"
echo $INIT_MYSQL_SQL_PATH
sed -i -e 's!INIT_MYSQL_SQL_PATH!'${INIT_MYSQL_SQL_PATH}'!' ./infrastructure/staging/gcp/k8s/db-deployment.yml
kubectl apply -f ./infrastructure/staging/gcp/k8s/db-secret.yml
kubectl apply -f ./infrastructure/staging/gcp/k8s/db-deployment.yml

kubectl create -f "https://raw.githubusercontent.com/DataDog/datadog-agent/master/Dockerfiles/manifests/rbac/clusterrole.yaml"
kubectl create -f "https://raw.githubusercontent.com/DataDog/datadog-agent/master/Dockerfiles/manifests/rbac/serviceaccount.yaml"
kubectl create -f "https://raw.githubusercontent.com/DataDog/datadog-agent/master/Dockerfiles/manifests/rbac/clusterrolebinding.yaml"
kubectl apply -f ./infrastructure/staging/gcp/k8s/datadog-secret.yml
kubectl apply -f ./infrastructure/staging/gcp/k8s/datadog-deployment.yml

kubectl apply -f ./infrastructure/staging/gcp/k8s/app-deployment-local.yml