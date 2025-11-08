# AWS KMSのキーペアをCloudFormationで作成する場合

- CloudFormationでKMSのキーペアを作る場合は、以下を実行するとスタックが作成される
- デフォルトの鍵は、楕円曲線アルゴリズム（`ECC_NIST_P256`）で署名検証用（`SIGN_VERIFY`）のキーペアが作成される
    ```sh
    # デフォルトのリージョンは東京リージョン（ap-northeast-1）に設定している
    cd cfn
    aws cloudformation validate-template --template-body file://cfn-kms.yaml
    aws cloudformation create-stack --stack-name Demo-KMS-Stack --template-body file://cfn-kms.yaml
    ```
- RSAアルゴリズム等他の暗号化アルゴリズムでキーペアを作成する場合は、`--parameters`オプションで`KeySpec`パラメータおよび`KeyUsage`パラメータを指定する    
    - 例：RSA_2048で署名検証用のキーペアを作成する場合

    ```sh
    aws cloudformation create-stack --stack-name Demo-KMS-Stack --template-body file://cfn-kms.yaml --parameters ParameterKey=KeySpec,ParameterValue=RSA_2048
    ```

- キー仕様の詳細は、[AWS KMS キーの仕様](https://docs.aws.amazon.com/ja_jp/kms/latest/developerguide/symm-asymm-choose-key-spec.html)を参照

- （オプション）マルチリージョンキーに対応して、別リージョンにレプリカキーを作成（例：東京リージョンのプライマリキーから大阪リージョンに作成）する場合は、以下のように`--region`オプションを付与する

```sh
# プライマリキーのARNを指定してレプリカキーを作成
# プライマリキーのARNは、Demo-KMS-Stack の出力結果から確認できる

aws cloudformation validate-template --template-body file://cfn-kms-replica.yaml
aws cloudformation create-stack --stack-name Demo-KMS-Replica-Stack --template-body file://cfn-kms-replica.yaml --region ap-northeast-3 --parameters ParameterKey=PrimaryKeyArn,ParameterValue=<プライマリキーのARN>
```

- キー作成後は、以下のツールでCSRや自己署名証明書を作成する
    - [DigitalSignCertificateToolByExistingKeyTest.java](../src/test/java/com/example/DigitalSignCertificateToolByExistingKeyTest.java)
        - SpringBootTestの形式で作成されていて、JUnitを実行することで、AWS KMSでキーペア・CSR・自己署名の証明書の作成するようになっている。
    - デフォルトは楕円曲線アルゴリズムを前提とした設定であるため、RSAに対応する場合は、[application-dev.yml](../src/main/resources/application-dev.yml)を修正する        

- スタックを削除する場合

```sh
# （オプション）レプリカキーを削除する場合
aws cloudformation delete-stack --stack-name Demo-KMS-Replica-Stack --region ap-northeast-3

# プライマリキーを削除
aws cloudformation delete-stack --stack-name Demo-KMS-Stack
```
