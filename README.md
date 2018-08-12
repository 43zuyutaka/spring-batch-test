# spring-batch-test

## Spring Batch メモ
https://spring.io/guides/gs/sts/

https://spring.io/guides/gs/batch-processing/

Maven だとエラーが出た。junitのプラグインが無いため？
Pom.xml に以下を追加して右クリック`＞maven>maven install`
```:xml
<dependency>
  <groupId>junit</groupId>
  <artifactId>junit</artifactId>
  <version>4.12</version>
  <scope>test</scope>
</dependency>
```

https://github.com/junit-team/junit4/wiki/download-and-install  より

STSの設定のせいでJavaのターゲットが１.５になっていたので修正

STSのmaven実行ターゲットを　`spring-boot:run` として実行（チュートリアルのmvnwコマンドを参考にした）

jdbcTemplateって？

### 参考
Eclipseプロジェクトやmaven設定で役立ちそう

http://hiranoon.hatenablog.com/entry/2015/12/30/160514


—-----
## 試すこと
* バッチをもうひとつつくる
* configファイルのxml化
* 自分でバッチを作ってみる
** チャンクモデル
** タスクレットモデル
* Mybatis利用（DB）
* IF変更（CSV、DB、フラットなファイル）
* DBを変える（OracleXE）
* ビルドの方法
* テスト

