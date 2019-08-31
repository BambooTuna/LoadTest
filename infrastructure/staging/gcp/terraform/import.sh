. ../gcloud-env
terraform -v
terraform init
terraform import -var-file=../gcloud-env google_compute_network.default ${GOOGLE_PROJECT_ID}
terraform import -var-file=../gcloud-env google_compute_subnetwork.default ${GOOGLE_PROJECT_ID}
terraform import -var-file=../gcloud-env google_container_cluster.default ${GOOGLE_PROJECT_ID}/${GOOGLE_COMPUTE_ZONE}/${GOOGLE_CLUSTER_NAME}
true