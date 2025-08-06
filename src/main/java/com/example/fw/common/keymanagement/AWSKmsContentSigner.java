package com.example.fw.common.keymanagement;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;

import com.example.fw.common.keymanagement.config.KeyManagementConfigurationProperties;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsAsyncClient;

/**
 * Bouncy CastleのContentSignerインターフェースを実装した AWS KMSを使用してコンテンツに署名するためのクラス<br>
 * 
 * CSR（証明書署名要求）や自己署名証明書を作成する際に、各コンテンツに署名するために使用されます。<br>
 */
public class AWSKmsContentSigner implements ContentSigner {
    private final KmsAsyncClient kmsAsyncClient;
    private final KeyInfo keyInfo;
    private final KeyManagementConfigurationProperties keyManagementConfigurationProperties;
    private final AlgorithmIdentifier algorithmIdentifier;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public AWSKmsContentSigner(KmsAsyncClient kmsAsyncClient, KeyInfo keyInfo,
            KeyManagementConfigurationProperties keyManagementConfigurationProperties) {
        this.kmsAsyncClient = kmsAsyncClient;
        this.keyInfo = keyInfo;
        this.keyManagementConfigurationProperties = keyManagementConfigurationProperties;
        this.algorithmIdentifier = new DefaultSignatureAlgorithmIdentifierFinder()
                .find(keyManagementConfigurationProperties.getSignatureAlgorithm());
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return algorithmIdentifier;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public byte[] getSignature() {
        byte[] dataToSign = outputStream.toByteArray();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(keyManagementConfigurationProperties.getHashAlgorithm());
            byte[] hash = messageDigest.digest(dataToSign);
            return kmsAsyncClient.sign(builder -> builder//
                    .keyId(keyInfo.getKeyId()) // キーIDを指定
                    .message(SdkBytes.fromByteArray(hash)) // ハッシュ値をメッセージとして指定
                    .signingAlgorithm(keyManagementConfigurationProperties.getAwsKms().getKmsSigningAlgorithmSpec())) // 署名アルゴリズムを指定
                    .thenApply(response -> {
                        // 署名の結果を取得
                        return response.signature().asByteArray();
                    }).join(); // 非同期処理の完了を待つ

        } catch (NoSuchAlgorithmException e) {
            // TODO 例外の定義
            throw new RuntimeException("Unsupported hash algorithm", e);

        }

    }

}
