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