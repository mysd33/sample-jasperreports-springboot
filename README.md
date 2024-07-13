# Jasper Reportsによる帳票出力のSpring Boot APサンプル
* Jasper Reportsを使って、帳票出力を行うためのサンプルAPです。Jasper Reportsの勉強するために作成しています。

## サンプルAPの起動方法
* Spring Boot APを起動します。
    * Spring Tool Suite（Eclipse）の場合
        * `JasperReportsSampleApplication.java`を右クリックして、`Run As` -> `Spring Boot Application`（または`Java Application`）を選択します。
        
    * コマンドラインの場合
        * プロジェクトのルートディレクトリに移動して、以下のコマンドを実行します。
            ```bash
            mvnw spring-boot:run
            ```

* ブラウザで以下のURLにアクセスします。
    * http://localhost:8080/

* トップ画面より、帳票名のリンクをクリックすると、PDFがダウンロード、表示されます。

## 帳票の例
* 今後、帳票を追加していく予定です

    * 商品一覧
        * 「items.pdf」という名前のPDFファイルがダウンロードされます。

        * 出力される帳票のイメージ
            ![items.pdf](image/items-report.png)


## 帳票様式の確認・編集方法
* Jasper Studioを使って帳票様式を確認・編集したい場合は、Jasper Studioをインストールしてください。Jasper Studioは、Eclipseベースの帳票デザインツールです。 

* Jasper Studioのダウンロード方法
    * [Jaspersoft Comunity Edtion ダウンロードサイト](https://community.jaspersoft.com/download-jaspersoft/community-edition/)からダウンロードします。
        * 会社のアドレスや情報を入れないとダウンロードできないようです。
        * Jaspersoftは個人との取引をしないということらしく、gmail等でアカウントと作ってしまうと[Access Deny](https://community.jaspersoft.com/access-denied/)になってしまうようです。

* ツールの使い方
    * [JasperSoftのコミュニティのドキュメントサイト](https://community.jaspersoft.com/documentation/)からドキュメントがダウンロードできます。
        * [Jasper StudioのHTMLドキュメントはこちら](https://community.jaspersoft.com/documentation/tibco-jasperreports-server-user-guide/v630/jaspersoft-studio-user-guide)

* サンプルAPの帳票様式について
    * まずは、ライブラリの利用方法の勉強のため、自分で作成せず、[参考情報](#参考情報)にあるサンプルAPのサイトの様式（jrxmlファイル）を利用させていただきました。
        * 今後、自分でJasper Studioを使って帳票様式を作成してみて勉強しながら、サンプルAPを充実化させていく予定です。

    * 様式ファイル（jrxmlファイル）は、Japsper Studio7.x.xから以前のバージョンとフォーマットが違うため、JasperReportsのライブラリも7.x.xを使うには、Jasper Studio7.x.xで作ったものである必要があるようです。
        * 既存のものはver6.x.x以前の形式だったので、Japsper Studio7.x.xで開きなおして、最新のver7.x系のフォーマットに変換したものを使っています。
  
## ライブラリの利用方法
* pom.xmlにライブラリの依存関係を追加
    * APで、JasperReportのライブラリを使用するには、まずpom.xmlで依存ライブラリを追加します。サンプルAPでは最新の7.x.xを利用しています。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project …>
    …
	<properties>
		<java.version>21</java.version>		
		<jasperreports.version>7.0.0</jasperreports.version>
	</properties>
	<dependencies>	
		<!-- Jaspter Reports -->
		<dependency>
			<groupId>net.sf.jasperreports</groupId>
			<artifactId>jasperreports</artifactId>
			<version>${jasperreports.version}</version>
			<exclusions>
				<exclusion>
					<artifactId>commons-logging</artifactId>
					<groupId>commons-logging</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		<!-- ver7.xより、機能ごとExtensionに分離されたので、追加定義が必要 -->
		<dependency>
			<groupId>net.sf.jasperreports</groupId>
			<artifactId>jasperreports-pdf</artifactId>
			<version>${jasperreports.version}</version>
			<exclusions>
				<exclusion>
					<artifactId>commons-logging</artifactId>
					<groupId>commons-logging</groupId>
				</exclusion>
			</exclusions>			
		</dependency>
   	</dependencies>
</project>        
```

* JasperのAPIの利用方法
    * とっかかりとして[参考情報](#参考情報)にあるサイトのサンプルAPを参考に、AP内でJasperReportのAPIを利用するといです。
    * 公式のサンプルコードやリファレンスは[JaspterReports LibraryのGitHubサイト](https://github.com/TIBCOSoftware/jasperreports?tab=readme-ov-file#jasperreports---free-java-reporting-library)を確認しまほう。

## 参考情報
* [Jaspersoft community editionの公式サイト](https://www.jaspersoft.com/products/jaspersoft-community)

* [NRIのOpenStandiaが提供するJaspersoft情報](https://openstandia.jp/oss_info/jaspersoft/)

* [Workbrain JAPAN](https://www.workbrainjapan.net/jasperreports-solution)
    * JasperReportsを扱っている、帳票・レポート作成ソリューションがある模様

* [JaspterReports LibraryのGitHubサイト](https://github.com/TIBCOSoftware/jasperreports)

* 日本語フォントの対応
    * [JasperReportでの日本語フォントの利用の記事](https://qiita.com/morya/items/26e1519b9ca813ed399a)
    * [IPAフォント](https://moji.or.jp/ipafont/ipafontdownload/)

* Japser Reportsを使ったチュートリアル、サンプル
    * サンプル1
        * https://howtodoinjava.com/spring-boot/spring-boot-jasper-report/
        * 完全なサンプルコード
            * https://github.com/lokeshgupta1981/Spring-Boot3-Demos/tree/main/jasper-reports-example

    * サンプル2
        * https://satyacodes.medium.com/spring-boot-reporting-with-jasper-reports-d4ed3128f0fe
        * 完全なサンプルコード
            * https://github.com/javaHelper/Build-Reports-with-JasperReports-Java-and-Spring-Boot

    * サンプル3
        * https://github.com/jamilxt/java_spring-boot_japser-report

    * サンプル4
        * https://www.baeldung.com/spring-jasper
        * 完全なサンプルコード
            * https://github.com/eugenp/tutorials/tree/master/libraries-reporting