# Jasper Reportsによる帳票出力を勉強するためのSpringBoot Apサンプル

## 使い方
* Spring Boot APを起動

* ブラウザで以下のURLにアクセス
  * http://localhost:8080/reports/items

* 「items.pdf」という名前のPDFファイルがダウンロードされる

    * 出力される帳票の例
    
    ![items.pdf](image/items-report.png)


## 参考情報
* [JasperReports Comunity Edtion ダウンロードサイト](https://community.jaspersoft.com/download-jaspersoft/community-edition/)

    * 会社のアドレスや情報を入れないと、Jaspersoft Studio等ダウンロードできない模様
        * JasperReportsは個人との取引をしないということらしい。gmail等で作ってしまうと、Access Denyになってしまう模様
            * https://community.jaspersoft.com/access-denied/

* [NRIのOpenStandiaが提供するJaspersoft情報](https://openstandia.jp/oss_info/jaspersoft/)

* [Workbrain JAPAN](https://www.workbrainjapan.net/jasperreports-solution)
    * JasperReportsを扱っている、帳票・レポート作成ソリューションがある模様

* [JaspterReportのGitHubサイト](https://github.com/TIBCOSoftware/jasperreports)

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