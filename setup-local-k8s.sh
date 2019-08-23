kubectl apply -f ./infrastructure/staging/gcp/k8s/db-secret.yml
kubectl apply -f ./infrastructure/staging/gcp/k8s/db-deployment.yml
kubectl apply -f ./infrastructure/staging/gcp/k8s/app-deployment-local.yml