高負荷に耐えるためにはどうしたらいいかを調べていく
## LocalTest
### Setup local k8s
```bash
$ docker build ./tools -t loadtest-local-db:latest
$ docker build . -t loadtest-local-k8s:latest
$ sh setup-local-k8s.sh
```


## API動作確認
```sbtshell
curl localhost/ping
curl -X POST -H "Content-Type: application/json" -d '{"name":"bambootuna","age":20}' localhost/user/add
```

## Gatlingテスト実行
```sbtshell
$ sbt clean gatling:test
```
## Dao実装の参考
[scala-ddd-base](https://github.com/j5ik2o/scala-ddd-base)

## Staging目次
- [CommonSetting](#CommonSetting)
- [CircleCI->DockerHub->GKE](#CircleCI->DockerHub->GKE)
- [GoogleCloudBuilder->GKE](#GoogleCloudBuilder->GKE)

### CommonSetting
1. enable Compute Engine API  
Go to [API & Service](https://console.cloud.google.com/apis/api/) and enable `Compute Engine API`.  

2. Adding credentials to a project
Using API => Computer Engine API
Are you planning to use this API on App Engine or Compute Engine? => Yes, using one or both.

3. Create Service Account  
Go to [credentials](https://console.cloud.google.com/apis/credentials) and create service account.  
Service Account => `Compute Engin default service account`  
Key Type => `Json`  
Rename credentials json file to `.account.json`.  
Move `.account.json` to `infrastructure/staging/gcp/terraform/.account.json`  

4. Install and setting Google-Cloud-SDK  
```bash
$ brew cask install google-cloud-sdk
$ gcloud config set project [your project id]
$ gcloud config set compute/region asia-northeast1
$ gcloud config set compute/zone asia-northeast1-a
$ gcloud auth activate-service-account --key-file=infrastructure/staging/gcp/terraform/.account.json
```

### CircleCI->DockerHub->GKE
1. Enable google services   
```bash
// GKE
$ gcloud services enable container.googleapis.com
// GRM
$ gcloud services enable cloudresourcemanager.googleapis.com
```

1. Encode account json to string
```sbtshell
$ base64 -i .account.json
1234567890asdfghjkl
```
2. Set encoded text to CircleCI env.
```
KeyName -> GCLOUD_SERVICE_KEY
Value -> 1234567890asdfghjkl
```


3. Import terraform



### GoogleCloudBuilder->GKE


1. Enable some services   
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

2. Register your Github repository  
[GSR](https://source.cloud.google.com/repo/new)  
The registered repository name would be used in [6].  
`ex. https://source.cloud.google.com/[your project id]/[repository name]`  

3. Install terraform  
```bash
$ brew install terraform
$ cd infrastructure/staging/gcp/terraform
```

4. Edit main.tf  
The repo_name should be replaced to [repository name].
```tf
variable "repo_name" {
    default = "[repository name]"
}
```

5. Run terraform
```bash
$ terraform init
...
$ terraform plan
...
$ terraform apply
...
```

6. Let's push something to your Github repository!
After pushed, see next page.  
[GCB](https://console.cloud.google.com/cloud-build/builds)  
Cloud builder should e moving.  