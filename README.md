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

### spring-mybatis設定
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

### mybatis設定

参考URL：http://www.mybatis.org/spring/ja/getting-started.html

とりあえずテーブルを準備
```
CREATE TABLE employee (
 id varchar(100),
 name varchar(100),
 note varchar(100),
 primary key ( id )
);
INSERT INTO employee VALUES ( 'id001', 'User111', 'note111');
INSERT INTO employee VALUES ( 'id002', 'User222', 'note222');
INSERT INTO employee VALUES ( 'id003', 'User333', 'note333');

```

```
public interface Smp1Mapper {
	@Select("SELECT * FROM employee WHERE id ='id001'")
	 EmployeeData getUser(@Param("userId") String userId);
}

とか

@Component("smp1ItemReader")
public class Smp1ItemReader implements ItemReader<EmployeeData> {
	private boolean flg = false;
	
	@Autowired
	private Smp1Mapper employeeMapper;
	
	@Override
	public EmployeeData read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		System.out.println("*** employee Reader start ***");
		EmployeeData data = employeeMapper.getUser("user001");
		if (flg == false) {
			flg = true;
			System.out.println("*** employee Reader done 111 ***");
			return data;
		} else {
			System.out.println("*** employee Reader done 222 ***");
			return null;
		}
	}
```

## jobRepositoryのクリーン
[参考URL](https://terasoluna-batch.github.io/guideline/5.0.1.RELEASE/ja/Ch04_JobParameter.html#Ch04_JobParameter_HowToUse_Converter)

[spring-batch-toolkit](https://github.com/arey/spring-batch-toolkit.git)
を使う

### インストール
```
git clone https://github.com/arey/spring-batch-toolkit.git
mvn clean install
```
2018/8/24時点ではJDK10で動かすとsurefire?のエラーがでるので、pomの設定を変える([参考](https://github.com/junit-team/junit4/issues/1513))
```
<version.plugin.maven-surefire-plugin>2.21.0</version.plugin.maven-surefire-plugin><version.plugin.maven-surefire-plugin>2.20.1</version.plugin.maven-surefire-plugin>
↓
<version.plugin.maven-surefire-plugin>2.21.0</version.plugin.maven-surefire-plugin><version.plugin.maven-surefire-plugin>2.21.0</version.plugin.maven-surefire-plugin>

```

#### やってみる①
* taskletの定義

```
	<batch:job id="jobRepositoryCleanJob">
		<batch:step id="jobRepositoryCleanJob_step1">
			<batch:tasklet transaction-manager="adminTransactionManager"
				start-limit="100" ref="removeSpringBatchHistoryTasklet">
			</batch:tasklet>
		</batch:step>
	</batch:job>
```
* RemoveSpringBatchHistoryTaskletをBeanとして登録。また、jdbcTemplateのBean登録が必要
```
	<bean id="removeSpringBatchHistoryTasklet"
		class="com.javaetmoi.core.batch.tasklet.RemoveSpringBatchHistoryTasklet">
		<property name="jdbcTemplate" ref="adminJdbcTemplate" />
		<property name="historicRetentionMonth" value="0" />
	</bean>

	<bean id="adminJdbcTemplate"
		class="org.springframework.jdbc.core.JdbcTemplate">
		<property name="dataSource" ref="adminDataSource" />
	</bean>
```

* エラーが。
transaction-managerはjdbcTemplateとdataSourceを合わせないとjobRepositoryのdeleteができない。

また、削除対象のjobRepositoryを使って起動したバッチではエラーが出てロールバックされてしまう。
実行中の自分のレコードが削除されているためと思われる。

#### やってみる② spring-batch-toolkitを使うだけにする
springのテンプレートをgit cloneする。[参考](https://spring.io/guides/gs/batch-processing/)。
[URL](https://github.com/arey/spring-batch-toolkit.git)に従ってspring-batch-toolkitをpomに追加
```
<dependency>
  <groupId>com.javaetmoi.core</groupId>
  <artifactId>spring-batch-toolkit</artifactId>
  <version>4.0.0</version>
</dependency> 
```
また、ジョブを動かすのに必要なモジュールもpomに追加
```
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-dbcp2</artifactId>
			<version>2.5.0</version>
		</dependency>
		<dependency>
			<groupId>com.oracle.jdbc</groupId>
			<artifactId>ojdbc7</artifactId>
			<version>12.1.0.2</version>

```
このバッチ自体のjobRepositoryはadminDatasource(hsqldb)で、このバッチで削除するjobRepositoryへの接続はdataSource(oracle)で接続する。
また、ジョブのtransactionManagerとdataSourceの設定はあわせないとよくわからない動作をする

* 実行 プロジェクトを右クリックから` Run Configuration`
  *  projectを選択
  *  Main Classに`org.springframework.batch.core.launch.support.CommandLineJobRunner`
  *  arguments>Program argumentsに`classpath:/launch-context.xml jobRepositoryCleanJob`
  * `Run`


* 動作確認
```
sqlplus batchadmin/batchadminpass@localhost:1521/xe
> select * from batch_job_instance
no rows selcted.
```
## 実行可能なJARの作成と実行

参考URLリスト
* [TERASOLUNA(実行方法)](https://terasoluna-batch.github.io/guideline/5.0.0.RELEASE/ja/Ch04_SyncJob.html)
* [JARファイルの実行メモ(hishidama memo)](http://www.ne.jp/asahi/hishidama/home/tech/java/jar.html)
* []()

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
# gradle

## install
[参考](https://gradle.org/install/)
```
brew install gradle
```

## 依存ライブラリ追加
`build.gradle`に追加。mavenのdependensiesのgroupid,artifactid,versionをコロンで区切って記述する。

ojdbcのような外部から持ってきたJARはローカルのmavenリポジトリにインストールして参照する方法と、以下の様に特定のパスを指定する方法がある。（ojdbcはORACLEのmavenリポジトリがあるみたい。要アカウント）

```
dependencies {
    compile("org.springframework.boot:spring-boot-starter-batch")
    compile("org.hsqldb:hsqldb")
    testCompile("junit:junit")
    //dbcp
    compile("org.apache.commons:commons-dbcp2:2.5.0")
    
    compile("org.mybatis:mybatis-spring:1.3.2")
    compile("org.mybatis:mybatis:3.4.6")
    compile files('libs/ojdbc7.jar')
    
    //jobRepojitoryのクリーン
    compile("com.javaetmoi.core:spring-batch-toolkit:4.0.0")
    
}
```

## ビルド
バッチ用の[ブランクプロジェクト](https://spring.io/guides/gs/batch-processing/)のサイトの手順通り。

`gradle build`でプロジェクトのbuild/libsにjarができる。

bootJarの設定にある名前でjarファイルが作成される。
```
bootJar {
    baseName = 'gs-batch-processing'
    version =  '0.1.0'
}
```


# maven色々メモ

* 依存性の確認
```
mvn dependency:tree
```

—-----
## 試すこと
* バッチをもうひとつつくる　OK
* configファイルのxml化　OK
* 自分でバッチを作ってみる
  * チャンクモデル　OK
  * タスクレットモデル OK
* Mybatis利用（DB）OK
 * h2とoracleの切替できる様にする
* IF変更（CSV、DB、フラットなファイル）
* DBを変える（OracleXE）OK
* jobRepositoryの初期化　OK
* ジョブを再実行可能にする（TERASOLUNA方式）
* gradleお試し
 * 基本のプロジェクトで利用　OK
* ビルドの方法
* テスト

