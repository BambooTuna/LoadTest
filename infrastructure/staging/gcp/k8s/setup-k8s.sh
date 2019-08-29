# replace env
sed -i -e 's!IMAGE_NAME!'${IMAGE_NAME}'!' ./app-deployment.yml
sed -i -e 's!DB_IMAGE!'${DB_IMAGE}'!' ./db-deployment.yml

kubectl create serviceaccount --namespace kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller

# setup datadog
helm init --service-account tiller
helm upgrade --install dd-agent --set datadog.apiKey=${DD_API_KEY} -f ./helm/datadog-values.yaml stable/datadog

# setup db
kubectl apply -f ./db-secret.yml
kubectl apply -f ./db-deployment.yml

# setup app
kubectl apply -f ./app-deployment.yml

# view logs
kubectl get svc,pod,rc