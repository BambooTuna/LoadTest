# replace env
sed -i -e 's!IMAGE_NAME!'${IMAGE_NAME}'!' ./app-deployment.yml

kubectl create serviceaccount --namespace kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller
sleep 20s

# kubectl apply -f ./requests-memory.yaml

# setup datadog
helm upgrade --install dd-agent --set datadog.apiKey=${DD_API_KEY} -f ./helm/datadog-values.yaml stable/datadog
# helm upgrade --install db-service --set image=${DB_IMAGE} --set imageTag=${DB_TAG} -f ./helm/mysql-values.yaml stable/mysql

helm upgrade --install redis-user1 -f ./helm/redis-values.yaml stable/redis
helm upgrade --install redis-user2 -f ./helm/redis-values.yaml stable/redis
helm upgrade --install redis-user3 -f ./helm/redis-values.yaml stable/redis

helm upgrade --install redis-adid1 -f ./helm/redis-values.yaml stable/redis
helm upgrade --install redis-adid2 -f ./helm/redis-values.yaml stable/redis
helm upgrade --install redis-adid3 -f ./helm/redis-values.yaml stable/redis

helm upgrade --install redis-budget -f ./helm/redis-values.yaml stable/redis

# setup db
kubectl apply -f ./db-secret.yml
# kubectl apply -f ./db-deployment.yml

# setup app
kubectl apply -f ./app-deployment.yml

# view logs
kubectl get svc,pod,rc