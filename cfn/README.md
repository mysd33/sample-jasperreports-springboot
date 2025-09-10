# AWS KMSのキーペアをCloudFormationで作成する場合

- CloudFormationでKMSのキーペアを作る場合は、以下を実行するとスタックが作成される

```sh
cd cfn
aws cloudformation validate-template --template-body file://cfn-kms.yaml
aws cloudformation create-stack --stack-name Demo-KMS-Stack --template-body file://cfn-kms.yaml
```
- キー作成後は、以下のツールでCSRや自己署名証明書を作成する
  - [DigitalSignCertificateToolByExistingKeyTest.java](../src/test/java/com/example/DigitalSignCertificateToolByExistingKeyTest.java)
    - SpringBootTestの形式で作成されてて、JUnitを実行することで、AWS KMSでキーペア・CSR・自己署名の証明書の作成するようになっている。
    - キーエイリアスは`alias/digital-signature-key`で既存のキーを取得するので、キーエリアスを変更した場合は、コードも修正すること

- スタックを削除する場合

```sh
aws cloudformation delete-stack --stack-name Demo-KMS-Stack
```
