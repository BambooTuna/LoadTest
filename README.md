高負荷に耐えるためにはどうしたらいいかを調べていく


## 動作確認
```sbtshell
$ docker-compose up -d --build
```
※[docker-composeのインストールなど](https://blog.bambootuna.com/%E5%82%99%E8%80%83%E9%8C%B2/22/#toc3)

```sbtshell
curl localhost/ping
curl -X POST -H "Content-Type: application/json" -d '{"name":"bambootuna","age":20}' localhost/json
```

## Gatlingテスト実行
```sbtshell
$ sbt clean gatling:test
```