# AWS KMSでキーペア・CSR・自己署名の証明書の作成

1. 鍵だけCloudFormationで作成しておく場合（おすすめ）
    - キーペアのみCloudFormationで作成するようになっている
    - CloudFormationの利点として、スタック管理されるので、開発時等、KMSのキーのスタックを消せば、リソース＝キーも消えるので便利である。
    - [cfn](../../cfn/)のフォルダにCloudFormationTemplateがある。
    - 鍵を作成後は、[DigitalSignCertificateToolByExistingTest.java](../../src/test/java/com/example/DigitalSignCertificateToolByExistingKeyTest.java.java)でJUnitを実行することで、AWS KMSでCSR・自己署名の証明書の作成する。
    使い方は[README.md](../../cfn/README.md)を参照すること。

1. 鍵を含めてJavaアプリケーションで作成する場合
    - 以下に作成ツールがあり、JUnitを実行することで、AWS KMSでキーペア・CSR・自己署名の証明書の作成する。
        - [DigitalSignCertificateToolTest.java](../../src/test/java/com/example/DigitalSignCertificateToolTest.java)
        - SpringBootTestの形式で作成されてて、JUnitを実行することで、AWS KMSでキーペア・CSR・自己署名の証明書の作成するようになっている。
    - CloudFormationと違い、都度、ユーザがアプリケーション内で制御してキーペアも作成する場合には便利である。また、スタックを作らず、APIと叩くだけなので、非常に高速にキーが作成される。
        - ただし、キーは手動で削除する必要があるのと、猶予期間最低7日間までは削除されないので、開発時は注意。
