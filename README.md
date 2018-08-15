# spring-batch-test
# Spring Batch メモ
##　導入
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

## SampleBatchの移殖
### SampleBatch実行
https://qiita.com/yu_eguchi/items/5f93a3e703030d7e472d

project＞new＞spring legacy project＞Templates でsimple spring batch project

PJコンテキストメニュー＞Run Configuration

メインクラス　org.springframework.batch.core.launch.support.CommandLineJobRunner

引数　classpath:launch-context.xml job1

###　URL[https://qiita.com/yu_eguchi/items/5f93a3e703030d7e472d]に従って作ったJobを移植する
SampleBatchはSpringBatch2.3なので、4.0のprojectで動かしてみる

以下のファイルをコピー
* EmployeeProcessor.java
* EmployeeWriter.java
* ExampleConfiguration.java dataSourceなどのComponentを定義しているので必要
* EmployeeData.java
* module-context.xml
* batch.properties
* launch-context.xml
* log4j.properties あとでslf4jに変えたい

pom.xmlにExampleConfiguraion.javaの依存ファイル追加
`
		<dependency>
			<groupId>commons-dbcp</groupId>
			<artifactId>commons-dbcp</artifactId>
			<version>1.2.2</version>
		</dependency>
`
あとは同様に　Run Configuration で実行


# 参考
Eclipseプロジェクトやmaven設定で役立ちそう

http://hiranoon.hatenablog.com/entry/2015/12/30/160514


—-----
## 試すこと
* バッチをもうひとつつくる　OK
* configファイルのxml化　OK
* 自分でバッチを作ってみる
** チャンクモデル
** タスクレットモデル
* Mybatis利用（DB）
* IF変更（CSV、DB、フラットなファイル）
* DBを変える（OracleXE）
* ビルドの方法
* テスト

