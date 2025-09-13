# AWS KMSのキーペアをCloudFormationで作成する場合

- CloudFormationでKMSのキーペアを作る場合は、以下を実行するとスタックが作成される

```sh
# デフォルトのリージョンは東京リージョン（ap-northeast-1）に設定している
cd cfn
aws cloudformation validate-template --template-body file://cfn-kms.yaml
aws cloudformation create-stack --stack-name Demo-KMS-Stack --template-body file://cfn-kms.yaml
```
- （オプション）マルチリージョンキーに対応して、別リージョンにレプリカキーを作成（例：東京リージョンのプライマリキーから大阪リージョンに作成）する場合は、以下のように`--region`オプションを付与する

```sh
# プライマリキーのARNを指定してレプリカキーを作成
# プライマリキーのARNは、Demo-KMS-Stack の出力結果から確認できる

aws cloudformation validate-template --template-body file://cfn-kms-replica.yaml
aws cloudformation create-stack --stack-name Demo-KMS-Replica-Stack --template-body file://cfn-kms-replica.yaml --region ap-northeast-3 --parameters ParameterKey=PrimaryKeyArn,ParameterValue=<プライマリキーのARN>
```

- キー作成後は、以下のツールでCSRや自己署名証明書を作成する
  - [DigitalSignCertificateToolByExistingKeyTest.java](../src/test/java/com/example/DigitalSignCertificateToolByExistingKeyTest.java)
    - SpringBootTestの形式で作成されてて、JUnitを実行することで、AWS KMSでキーペア・CSR・自己署名の証明書の作成するようになっている。
    - キーエイリアスは`alias/digital-signature-key`で既存のキーを取得するので、キーエリアスを変更した場合は、コードも修正すること

- スタックを削除する場合

```sh
# （オプション）レプリカキーを削除する場合
aws cloudformation delete-stack --stack-name Demo-KMS-Replica-Stack --region ap-northeast-3

# プライマリキーを削除
aws cloudformation delete-stack --stack-name Demo-KMS-Stack
```
