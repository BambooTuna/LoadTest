# replace env
sed -i -e 's!IMAGE_NAME!'${IMAGE_NAME}'!' ./app-deployment.yml
# sed -i -e 's!DB_IMAGE!'${DB_IMAGE}'!' ./db-deployment.yml

kubectl create serviceaccount --namespace kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller
sleep 20s

# kubectl apply -f ./requests-memory.yaml

# setup datadog
helm upgrade --install dd-agent --set datadog.apiKey=${DD_API_KEY} -f ./helm/datadog-values.yaml stable/datadog
helm upgrade --install db-service --set image=${DB_IMAGE} --set imageTag=${DB_TAG} -f ./helm/mysql-values.yaml stable/mysql
helm upgrade --install redis-service -f ./helm/redis-values.yaml stable/redis
helm upgrade --install aerospike-service -f ./helm/aerospike-values.yaml stable/aerospike

# setup db
kubectl apply -f ./db-secret.yml
# kubectl apply -f ./db-deployment.yml

# setup app
kubectl apply -f ./app-deployment.yml

# view logs
kubectl get svc,pod,rc