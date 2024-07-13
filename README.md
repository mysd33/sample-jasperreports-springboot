# Jasper Reportsによる帳票出力を勉強するためのSpringBoot Apサンプル

## 帳票様式の確認・編集について
* Jasper Studioを使って帳票様式を確認・編集したい場合には、以下の手順を実施します。
* Jasper Studioは、Eclipseベースの帳票デザインツールです。

* [Jaspersoft Comunity Edtion ダウンロードサイト](https://community.jaspersoft.com/download-jaspersoft/community-edition/)からダウンロードします。
    * 会社のアドレスや情報を入れないと、Jaspersoft Studio等ダウンロードできない
        * Jaspersoftは個人との取引をしないということらしく、gmail等でアカウントと作ってしまうと[Access Deny](https://community.jaspersoft.com/access-denied/)になってしまうようです。

* 使い方については[JasperSoftのコミュニティ](https://community.jaspersoft.com/documentation/)からドキュメントがダウンロードできます。
    * [Jasper StudioのHTMLドキュメントはこちら](https://community.jaspersoft.com/documentation/tibco-jasperreports-server-user-guide/v630/jaspersoft-studio-user-guide)

* 現状、[参考情報](#参考情報)にあるサンプルAPのサイトの様式をJapsper Studioで開きなおして、最新のver7.x系のフォーマットに変換したものを使っています。
    * 今後、自分で帳票様式を作成して試してサンプルAPを充実化させていく予定です。

## サンプルAPの起動方法
* Spring Boot APを起動します。
    * Spring Tool Suite（Eclipse）の場合
        * `JasperReportsSampleApplication.java`を右クリックして、`Run As` -> `Spring Boot Application`または`Java Application`を選択します。
        
    * コマンドラインの場合
        * プロジェクトのルートディレクトリに移動して、以下のコマンドを実行します。
            ```bash
            mvnw spring-boot:run
            ```

* ブラウザで以下のURLにアクセス
    * http://localhost:8080/

    * メニュー画面より帳票名のリンクをクリック
        * 商品一覧
            * 「items.pdf」という名前のPDFファイルがダウンロードされます。

            * 出力される帳票のイメージ
                ![items.pdf](image/items-report.png)
        * TODO: 今後、帳票を追加していく予定        


## 参考情報
* [NRIのOpenStandiaが提供するJaspersoft情報](https://openstandia.jp/oss_info/jaspersoft/)

* [Workbrain JAPAN](https://www.workbrainjapan.net/jasperreports-solution)
    * JasperReportsを扱っている、帳票・レポート作成ソリューションがある模様

* [JaspterReportのGitHubサイト](https://github.com/TIBCOSoftware/jasperreports)

* 日本語フォントの対応
    * [JasperReportでの日本語フォントの利用の記事](https://qiita.com/morya/items/26e1519b9ca813ed399a)
    * [IPAフォント](https://moji.or.jp/ipafont/ipafontdownload/)

* Japser Reportを使ったチュートリアル、サンプル
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