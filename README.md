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

###　[URL](https://qiita.com/yu_eguchi/items/5f93a3e703030d7e472d)に従って作ったJobを移植する

SampleBatchはSpringBatchバージョン2.3なので、4.0のprojectで動かしてみる

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
```
		<dependency>
			<groupId>commons-dbcp</groupId>
			<artifactId>commons-dbcp</artifactId>
			<version>1.2.2</version>
		</dependency>
```

追記:commons-dbcpの最新は最新は２.５みたい。
```
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-dbcp2</artifactId>
    <version>2.5.0</version>
</dependency>

```

module-context.xmlの名前空間修正

```
http://www.springframework.org/schema/batch http://www.springframework.org/schema/batch/spring-batch-2.1.xsd
```

```
http://www.springframework.org/schema/batch http://www.springframework.org/schema/batch/spring-batch.xsd
```


あとは同様に　Run Configuration で実行

### log4j->slf4j
```
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
・・・
	private static final Log log = LogFactory.getLog(EmployeeWriter.class);
```
↓
```
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
・・・
	private static final Logger log = LoggerFactory.getLogger(EmployeeWriter.class);
```
log4j.projertiesは削除してOK

## バッチをつくってみる
### チャンクモデル
サンプル参照

### タスクレットモデル

taskletインタフェースを実装したクラスを作成

tasklet用の設定ファイル作成

module-context.xmlに以下を定義
```
<batch:job id="taskletjob1">
    <batch:step id="taskletjob1_step1">
        <batch:tasklet transaction-manager="transactionManager" start-limit="100" ref="taskletSample">
        </batch:tasklet>
    </batch:step>
</batch:job>
```

実行はチャンクモデルの場合と一緒

# Mybatis利用
oracleの環境は構築済み。
[ここ](http://www.mybatis.org/spring/ja/getting-started.html)
を見ながら設定

### datasource設定
pom.xml
```
	<dependency>
		<groupId>org.mybatis</groupId>
		<artifactId>mybatis-spring</artifactId>
		<version>1.3.2</version>
	</dependency>
	<dependency>
		<groupId>org.mybatis</groupId>
		<artifactId>mybatis</artifactId>
		<version>3.4.6</version>
	</dependency>
```

launch-context.xml

datasourceの設定をmybatis-springのサイトを参考にして実施。[xmlでエラーがでるのはnamgespaceの設定が足りないせいらしい。](http://matsukaz.hatenablog.com/entry/20080108/1199806929)

`xmlns:p="http://www.springframework.org/schema/p"`を追加

```
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:batch="http://www.springframework.org/schema/batch"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:jdbc="http://www.springframework.org/schema/jdbc"
	xmlns:p="http://www.springframework.org/schema/p"
	xsi:schemaLocation="
・・・
```

TERASOLUNAへのoracle設定を参考にpom.xmlに以下を追加
```
		<dependency>
			<groupId>com.oracle.jdbc</groupId>
			<artifactId>ojdbc7</artifactId>
			<version>12.1.0.2</version>
		</dependency>
```

context:property-placeholderにlocationだけ設定しても読み込んでくれない。これもTERASOLUNAのプロパティのオプション設定を参考にする
```
	<context:property-placeholder
		location="classpath:db.properties" system-properties-mode="OVERRIDE"
		ignore-resource-not-found="false" ignore-unresolvable="true" order="1" />
```

データソース、transactionManagerの設定も追加

```
<batch:job-repository id="jobRepository"
            data-source="adminDataSource"
            transaction-manager="adminTransactionManager"
            isolation-level-for-create="READ_COMMITTED"/>
            
<bean id="adminTransactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager"
          p:dataSource-ref="adminDataSource"
          p:rollbackOnCommitFailure="true"/>
<bean id="sqlSessionFactory"
		class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
</bean>
```



# 参考
Eclipseプロジェクトやmaven設定で役立ちそう

http://hiranoon.hatenablog.com/entry/2015/12/30/160514

# ちなみに[TELASOLUNA](https://terasoluna-batch.github.io/guideline/5.0.0.RELEASE/ja/Ch03_CreateProject.html#Ch03_CreateProject_blank)

ドキュメントのとおり実行すればOKだが、JDK9からJ2EEのクラスを参照できなくなったため、validationのエラーがでる

[ここ](https://qiita.com/ukiuni@github/items/bf6b14e9aa1090ec4a75)を参考にして、javax.xml.validationをpom.xmlの依存性に加える。

mvn cleanしてから、TELASOLUNAの実行手順に従って実行すればOK

# macへOracleインストール
Docker使うのが一番楽そう

## MacへのOracleインストール（Docker使用）

[案１](https://qiita.com/sunack/items/b6f9337f173c8e48e299)

docker pull wnameless/oracle-xe-11g:16.04

→稼動確認OK

[案２](https://hub.docker.com/r/sath89/oracle-xe-11g/)

こっちの方がサイズが少ない＆スターが多い

## MacへSQL*plusのインストール
https://www.oracle.com/technetwork/topics/intel-macsoft-096467.html
instant clientとSQL*Plusをインストールする必要がある

手順もURL参照

sqlplusで接続する
```
sqlplus system/oracle@localhost:1521/xe
```
※ssh接続できない。ホスト側のせいっぽい？`docker exec -it`があるのであまり必要性は無くなったかも。sqlplusも接続できたし。


## oracleの初期設定
* ユーザ作成(batch/batchpass, batchadmin/batchadminpassを作成)

` CREATE USER batch IDENTIFIED BY "batchpass" DEFAULT TABLESPACE users TEMPORARY TABLESPACE temp ;`

`GRANT DBA TO batch;`

* ユーザのアクセスできるテーブル一覧

`select * from user_tables; `


## JDBCドライバの追加

オラクルのサイトからDLした後、mavenにインストールする。
[参考サイト](https://blog.y-yuki.net/entry/2016/11/24/000000)

```
mvn install:install-file -Dfile=ojdbc7.jar -DgroupId=com.oracle.jdbc -DartifactId=ojdbc7 -Dversion=12.1.0.2 -Dpackaging=jar
```

pom.xmlに以下を追加

```
<dependency>
	<groupId>com.oracle.jdbc</groupId>
	<artifactId>ojdbc7</artifactId>
	<version>12.1.0.2</version>
</dependency>
```

src/main/resources/batch-application.properties のデータソースの設定を以下の様に変更。adminとbatchデフォルトユーザの２か所

```
admin.jdbc.driver=oracle.jdbc.driver.OracleDriver
admin.jdbc.url=jdbc:oracle:thin:@localhost:1521:XE
admin.jdbc.username=batch
admin.jdbc.password=batchpass
・・・
jdbc.driver=oracle.jdbc.driver.OracleDriver
jdbc.url=jdbc:oracle:thin:@localhost:1521:XE
jdbc.username=batch
jdbc.password=batchpass

```

* jobRepositoryの設定

https://terasoluna-batch.github.io/guideline/5.0.0.RELEASE/ja/Ch03_CreateProject.html#Ch03_CreateProject_Make_Setting_DB
にDBによる設定変更箇所が書いてある

また、SpringBatchのjarにoracle用の初期化SQLがあるのでそれを使う。（githubで確認した）
```
spring-batch.schema.script=classpath:org/springframework/batch/core/schema-oracle10g.sql
```


## Dockerコマンド

* 初回の起動
```
docker run <image-name>
```
* 2回目以降起動
```
docker start <image-name>
```
* 停止
```
docker stop <image-name>
```
* docker確認(-aオプションを除くと起動中イメージのみ確認できる)
```
docker ps -a
```
* dockerのコンテナにシェルログイン
```
docker exec -it <image-name>
```

—-----
## 試すこと
* バッチをもうひとつつくる　OK
* configファイルのxml化　OK
* 自分でバッチを作ってみる
  * チャンクモデル　OK
  * タスクレットモデル OK
* Mybatis利用（DB）
* IF変更（CSV、DB、フラットなファイル）
* DBを変える（OracleXE）OK
* ビルドの方法
* テスト

