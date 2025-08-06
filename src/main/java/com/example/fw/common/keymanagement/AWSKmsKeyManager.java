package com.example.fw.common.keymanagement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import com.example.fw.common.keymanagement.config.KeyManagementConfigurationProperties;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.objectstorage.DownloadObject;
import com.example.fw.common.objectstorage.ObjectStorageFileAccessor;
import com.example.fw.common.objectstorage.UploadObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsAsyncClient;
import software.amazon.awssdk.services.kms.model.MessageType;
import software.amazon.awssdk.services.kms.model.SignRequest;

/**
 * AWSのKMSを使ってキー管理を行うKeyManager実装クラス
 * 
 */
@Slf4j
@RequiredArgsConstructor
public class AWSKmsKeyManager implements KeyManager {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final KmsAsyncClient kmsAsyncClient;
    private final ObjectStorageFileAccessor objectStorageFileAccessor;
    private final KeyManagementConfigurationProperties keyManagementConfigurationProperties;

    static {
        // BouncyCastleプロバイダを登録
        Security.addProvider(new BouncyCastleProvider());
    }

    // 参考：AWS SDK for JavaのKMSのサンプルコード例
    // https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/java_kms_code_examples.html
    // https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/kms#code-examples

    @Override
    public KeyInfo createKey() {
        // KMSを使って暗号鍵を生成する
        return kmsAsyncClient.createKey(builder -> builder//
                .keySpec(keyManagementConfigurationProperties.getAwsKms().getKeySpec())//
                .keyUsage(keyManagementConfigurationProperties.getAwsKms().getKeyUsage())
                .description(keyManagementConfigurationProperties.getAwsKms().getKeyDescription()))//
                .thenApply(response -> //
                KeyInfo.builder().//
                        keyId(response.keyMetadata().keyId()) // レスポンスからキーIDを取得
                        .state(response.keyMetadata().keyStateAsString()) // レスポンスからキーの状態を取得
                        .build())
                .join();
    }

    @Override
    public KeyInfo deleteKey(final KeyInfo keyInfo) {
        final String keyId = keyInfo.getKeyId();
        final int pendingWindowInDays = keyManagementConfigurationProperties.getAwsKms().getPendingDeleteWindowInDays();
        // KMSを使って暗号鍵を削除する
        return kmsAsyncClient.scheduleKeyDeletion(builder -> builder//
                .keyId(keyId).pendingWindowInDays(pendingWindowInDays))//
                .thenApply(response -> {
                    appLogger.debug("キー{}は{}後に削除します", keyId, pendingWindowInDays);
                    return KeyInfo.builder()//
                            .keyId(keyId) // レスポンスからキーIDを取得
                            .state(response.keyStateAsString()) // レスポンスからキーの状態を取得
                            .build();
                }).join();
    }

    @Override
    public PublicKey getPublicKey(final KeyInfo keyInfo) {
        return kmsAsyncClient.getPublicKey(builder -> builder//
                .keyId(keyInfo.getKeyId()))//
                .thenApply(response -> {
                    byte[] publicKeyBytes = response.publicKey().asByteArray();
                    KeyFactory keyFactory;
                    try {
                        keyFactory = KeyFactory
                                .getInstance(keyManagementConfigurationProperties.getKeyFactoryAlgorithm());
                        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                    } catch (NoSuchAlgorithmException e) {
                        // TODO 例外の定義
                        throw new RuntimeException("Unsupported key algorithm", e);
                    } catch (InvalidKeySpecException e) {
                        // TODO 例外の定義
                        throw new RuntimeException("Invalid key specification", e);
                    }
                }).join();
    }

    @Override
    public CertificateSigningRequest createCsr(final KeyInfo keyInfo, final String subject) {
        PublicKey publicKey = getPublicKey(keyInfo);
        X500Name subjectName = new X500Name(subject);
        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(subjectName, publicKey);
        PKCS10CertificationRequest csr = csrBuilder
                .build(new AWSKmsContentSigner(kmsAsyncClient, keyInfo, keyManagementConfigurationProperties));
        try {
            return CertificateSigningRequest.builder() //
                    .der(csr.getEncoded()) // CSRのDERエンコードされたバイト配列を設定
                    .build();
        } catch (IOException e) {
            // TODO 例外の定義
            throw new RuntimeException("Failed to encode CSR", e);
        }
    }

    @Override
    public Certificate createSelfSignedCertificate(final CertificateSigningRequest csr, final KeyInfo keyInfo) {
        try {
            PKCS10CertificationRequest pkcs10Csr = new PKCS10CertificationRequest(csr.getDer());
            // CSRからSubjectと公開鍵を抽出
            X500Name subject = pkcs10Csr.getSubject();
            byte[] pubKeyBytes = pkcs10Csr.getSubjectPublicKeyInfo().getEncoded();
            PublicKey publicKey = KeyFactory.getInstance(keyManagementConfigurationProperties.getKeyFactoryAlgorithm())
                    .generatePublic(new X509EncodedKeySpec(pubKeyBytes));
            X500Name issuer = subject; // 自己署名なのでIssuerはSubjectと同じ
            BigInteger serialNumber = BigInteger.valueOf(1);
            Date notBefore = new Date();
            Date notAfter = new Date(System.currentTimeMillis()
                    + keyManagementConfigurationProperties.getSelfSignedCertValidityDays() * 24 * 60 * 60 * 1000L);
            X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuer, serialNumber,
                    notBefore, notAfter, subject, publicKey);
            // Subject Key Identifierを取得
            JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
            certificateBuilder.addExtension(Extension.subjectKeyIdentifier, false,
                    extensionUtils.createSubjectKeyIdentifier(pkcs10Csr.getSubjectPublicKeyInfo()));
            // certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
            X509CertificateHolder certificateHolder = certificateBuilder
                    .build(new AWSKmsContentSigner(kmsAsyncClient, keyInfo, keyManagementConfigurationProperties));
            return Certificate.builder() //
                    .der(certificateHolder.getEncoded()) // 証明書のDERエンコードされたバイト配列を設定
                    .build();
        } catch (IOException e) {
            // TODO 例外の定義
            throw new RuntimeException("Failed to read CSR", e);
        } catch (NoSuchAlgorithmException e) {
            // TODO 例外の定義
            throw new RuntimeException("Unsupported key algorithm", e);
        } catch (InvalidKeySpecException e) {
            // TODO 例外の定義
            throw new RuntimeException("Invalid key specification", e);
        }
    }

    @Override
    public void saveCsrToObjectStorage(final CertificateSigningRequest csr, final KeyInfo keyInfo) { //
        final String certsBassPrefix = keyManagementConfigurationProperties.getCertsBasePrefix();
        final String certsCsrFileName = keyManagementConfigurationProperties.getCsrPemFileName();
        final String csrPrefix = certsBassPrefix + keyInfo.getKeyId() + "/" + certsCsrFileName;
        // PEM形式でCSRをエクスポート
        StringWriter csrWriter = new StringWriter();
        try {
            csr.exportPemTo(csrWriter);
        } catch (IOException e) {
            // TODO 例外の定義
            throw new RuntimeException("Failed to export CSR to PEM format", e);
        }
        appLogger.debug("CSR PEM: {}", csrWriter.toString());
        InputStream csrInputStream = new ByteArrayInputStream(csrWriter.toString().getBytes());
        // CSRのpemをS3にアップロード
        UploadObject csrUploadObject = UploadObject.builder().inputStream(csrInputStream).prefix(csrPrefix)
                .size(csrWriter.toString().getBytes().length).build();
        objectStorageFileAccessor.upload(csrUploadObject);
        appLogger.debug("CSRファイルをアップロード: {}", csrPrefix);
    }

    @Override
    public CertificateSigningRequest getCsrFromObjectStorage(final KeyInfo keyInfo) {
        final String certsBassPrefix = keyManagementConfigurationProperties.getCertsBasePrefix();
        final String certsCsrFileName = keyManagementConfigurationProperties.getCsrPemFileName();
        final String csrPrefix = certsBassPrefix + keyInfo.getKeyId() + "/" + certsCsrFileName;
        // オブジェクトストレージからCSRのpemをダウンロード
        DownloadObject downloadObject = objectStorageFileAccessor.download(csrPrefix);
        try {
            return CertificateSigningRequest.builder()//
                    .der(downloadObject.getInputStream().readAllBytes()).build();
        } catch (IOException e) {
            // TODO 例外の定義
            throw new RuntimeException("Failed to read CSR from object storage", e);
        }
    }

    @Override
    public void saveSelfSignedCertificateToObjectStorage(final Certificate certificate, final KeyInfo keyInfo) {
        final String certsBassPrefix = keyManagementConfigurationProperties.getCertsBasePrefix();
        final String selfSignedCertificateFileName = keyManagementConfigurationProperties
                .getSelfSignedCertPemFileName();
        final String certifacatePrefix = certsBassPrefix + keyInfo.getKeyId() + "/" + selfSignedCertificateFileName;
        // PEM形式で自己署名証明書をエクスポート
        StringWriter certWriter = new StringWriter();
        try {
            certificate.exportPemTo(certWriter);
        } catch (IOException e) {
            // TODO 例外の定義
            throw new RuntimeException("Failed to export self-signed certificate to PEM format", e);
        }
        appLogger.debug("自己署名証明書 PEM: {}", certWriter.toString());
        InputStream certInputStream = new ByteArrayInputStream(certWriter.toString().getBytes());
        // 自己署名証明書のpemをS3にアップロード
        UploadObject certUploadObject = UploadObject.builder().inputStream(certInputStream).prefix(certifacatePrefix)
                .size(certWriter.toString().getBytes().length).build();
        objectStorageFileAccessor.upload(certUploadObject);
        appLogger.debug("自己署名証明書ファイルをアップロード: {}", certifacatePrefix);
    }

    @Override
    public Certificate getSelfSignedCertificateFromObjectStorage(final KeyInfo keyInfo) {
        final String selfSignedCertificateFileName = keyManagementConfigurationProperties
                .getSelfSignedCertPemFileName();
        return getCertificateFromObjectStorage(keyInfo, selfSignedCertificateFileName);
    }

    @Override
    public Certificate getCertificateFromObjectStorage(final KeyInfo keyInfo) {
        final String certificateFileName = keyManagementConfigurationProperties.getCertPemFileName();
        return getCertificateFromObjectStorage(keyInfo, certificateFileName);
    }

    @Override
    public Signature createSignatureFromDigest(final byte[] hasData, final KeyInfo keyInfo) {
        SignRequest signRequest = SignRequest.builder()//
                .keyId(keyInfo.getKeyId())//
                .message(SdkBytes.fromByteArray(hasData)) // データをバイト配列として設定
                .signingAlgorithm(keyManagementConfigurationProperties.getAwsKms().getKmsSigningAlgorithmSpec())//
                .messageType(MessageType.DIGEST) // メッセージタイプをDIGESTに設定
                .build();
        return kmsAsyncClient.sign(signRequest)//
                .thenApply(response -> Signature.builder()//
                        .value(response.signature().asByteArray()).build())//
                .join();
    }

    /**
     * オブジェクトストレージから証明書を取得するメソッド
     * 
     * @param keyInfo
     * @param certificateFileName
     * @return
     */
    private Certificate getCertificateFromObjectStorage(final KeyInfo keyInfo, final String certificateFileName) {
        final String certsBassPrefix = keyManagementConfigurationProperties.getCertsBasePrefix();
        final String certifacatePrefix = certsBassPrefix + keyInfo.getKeyId() + "/" + certificateFileName;
        // オブジェクトストレージから自己署名証明書のpemをダウンロード
        DownloadObject downloadObject = objectStorageFileAccessor.download(certifacatePrefix);
        try {
            return Certificate.builder()//
                    .der(downloadObject.getInputStream().readAllBytes()) // 証明書のDERエンコードされたバイト配列を設定
                    .build();
        } catch (IOException e) {
            // TODO 例外の定義
            throw new RuntimeException("Failed to read self-signed certificate from object storage", e);
        }
    }

}
