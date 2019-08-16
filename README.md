高負荷に耐えるためにはどうしたらいいかを調べていく

## API動作確認
```sbtshell
curl localhost/ping
curl -X POST -H "Content-Type: application/json" -d '{"name":"bambootuna","age":20}' localhost/json
```

## Gatlingテスト実行
```sbtshell
$ sbt clean gatling:test
```


## Staging
### GCP
You now in ~/LoadTest directory.  
```bash
$ pwd
/~~~/LoadTest
```

1. enable Compute Engine API  
Go to [API & Service](https://console.cloud.google.com/apis/api/) and enable `Compute Engine API`.  

2. Create Service Account  
Go to [credentials](https://console.cloud.google.com/apis/credentials) and create service account.  
Service Account = `Compute Engin default service account`  
Key Type = `Json`  
Rename credentials json file to `.account.json`.  
Move `.account.json` to `infrastructure/staging/gcp/terraform/.account.json`  

3. Install and setting Google-Cloud-SDK  
```bash
$ brew cask install google-cloud-sdk
$ gcloud config set project [your project id]
$ gcloud config set compute/region asia-northeast1
$ gcloud config set compute/zone asia-northeast1-a
$ gcloud auth activate-service-account --key-file=infrastructure/staging/gcp/terraform/.account.json
```

4. Enable some services   
```bash
// GCB
$ gcloud services enable cloudbuild.googleapis.com
// GKE
$ gcloud services enable container.googleapis.com
// GCR
$ gcloud services enable containerregistry.googleapis.com
// GSR
$ gcloud services enable sourcerepo.googleapis.com
```

5. Register your Github repository  
[GSR](https://source.cloud.google.com/repo/new)  
The registered repository name would be used in [6].  
`ex. https://source.cloud.google.com/[your project id]/[repository name]`  

5. Install terraform  
```bash
$ brew install terraform
$ cd infrastructure/staging/gcp/terraform
```

6. Edit main.tf  
The repo_name should replace to [repository name].
```tf
variable "repo_name" {
    default = "[repository name]"
}
```

7. Run terraform
```bash
$ terraform init
...
$ terraform plan
...
$ terraform apply
...
```

8. Let's push something to your Github repository!
After pushed, see next page.  
[GCB](https://console.cloud.google.com/cloud-build/builds)  
Cloud builder should e moving.  