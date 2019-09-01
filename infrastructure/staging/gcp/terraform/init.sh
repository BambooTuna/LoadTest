cd infrastructure/staging/gcp/terraform
terraform init
terraform plan -var-file=../gcloud-env
terraform apply -auto-approve -var-file=../gcloud-env