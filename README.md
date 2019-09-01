# LoadTest
高負荷に耐えるためにはどうしたらいいかを調べていく

## Test
### Local setup
1. Build
Use `sbt-native-packager` to build docker image.
```bash
$ sbt docker:publishLocal
```

2. Run
```bash
// Use k8s
$ sh setup-local-k8s.sh

// Use docker-compose
$ docker-compose up -d --build
...
```

### Curl test
```bash
$ curl localhost/ping
{"message":"pong","error_messages":[]}

$ curl -X POST -H "Content-Type: application/json" -d '{"name":"bambootuna","age":20}' localhost/user/add
{"id":{"user_id":-4934742503425198802},"error_messages":[]}

$ curl -X GET -H "Content-Type: application/json" -d '{"user_id":1}' localhost/user/get
{"user":{"user_id":6795494702222648856,"name":"bambootuna","age":20},"error_messages":[]}
```

### Gatling Test
```sbtshell
$ sbt clean gatling:test
```

### Locust Test
```sbtshell
$ cd tools/locust
$ LOCUST_FILE_PATH="main.py" LOCUST_HOST=http://localhost:8080 sh setup.sh
```
Access `localhost:8089` and run the test.

### Other
- See JMX Metrics
```bash
$ jconsole service:jmx:rmi:///jndi/rmi://localhost:8999/jmxrmi
```

- Too many connections error(mysql)
    - Check max connections
        `show variables like "%max_connections%";`
    - set max connections
        `set global max_connections = 1000;`
    - Check connection process
        `show processlist;`
        
    - Change my.cnf and apply
        `service mysqld restart`
        or
        `systemctl restart mysqld`


## Staging
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
Rename credentials json file to `account.json`.  
Move `account.json` to `infrastructure/staging/gcp/terraform/account.json`  

4. Install and setting Google-Cloud-SDK  
```bash
$ brew cask install google-cloud-sdk
$ gcloud config set project [your project id]
$ gcloud config set compute/region asia-northeast1
$ gcloud config set compute/zone asia-northeast1-a
$ gcloud auth activate-service-account --key-file=infrastructure/staging/gcp/terraform/account.json
```

5. Add role
[IAM](https://console.cloud.google.com/iam-admin/)
`Compute Engine default service account` <- `Kubernetes Engine サービス エージェント`

### CircleCI->GCR->GKE
CircleCI  
|  
|-Use terraform to create GKE[k8s-cluster]  
|  
|-Push docker-image to GCR  
|  
|-Use helm to deploy datadog-agent to GKE  
|  
|-Use deployment.yml to deploy db, app to GKE  
|  
Done  

1. Enable google services   
```bash
// GKE
$ gcloud services enable container.googleapis.com
// GCR
$ gcloud services enable containerregistry.googleapis.com
// GRM
$ gcloud services enable cloudresourcemanager.googleapis.com
```

1. Encode account json to string
```sbtshell
$ base64 -i infrastructure/staging/gcp/terraform/account.json
1234567890asdfghjkl
```
2. Set encoded text to CircleCI env.
```
KeyName -> GCLOUD_SERVICE_KEY
Value -> 1234567890asdfghjkl

// [Datadog](https://app.datadoghq.com/account/settings#api) -> API Keys -> Create API Keys
KeyName -> DD_API_KEY
Value -> 1234567890asdfghjkl
```

3. Init terraform from local
If you have not created [GKE GRM] yet, do next command.
```bash
//Install terraform.
$ brew install terraform
...

//Create your environment useing terraform.
$ sh infrastructure/staging/gcp/terraform/init.sh
...
It will take some time.
...
```

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

## References
- DaoImpl
[scala-ddd-base](https://github.com/j5ik2o/scala-ddd-base)