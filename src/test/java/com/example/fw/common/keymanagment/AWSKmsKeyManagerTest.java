package com.example.fw.common.keymanagment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import java.security.PublicKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.fw.common.keymanagement.AWSKmsKeyManager;
import com.example.fw.common.keymanagement.Certificate;
import com.example.fw.common.keymanagement.CertificateSigningRequest;
import com.example.fw.common.keymanagement.KeyInfo;
import com.example.fw.common.keymanagement.KeyManager;
import com.example.fw.common.keymanagement.config.KeyManagementConfigurationProperties;
import com.example.fw.common.objectstorage.ObjectStorageFileAccessor;
import com.example.fw.common.objectstorage.ObjectStorageFileAccessorFake;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsAsyncClient;
import software.amazon.awssdk.services.kms.model.KeyState;

/**
 * AWSKmsKeyManagerのテストクラス
 */
class AWSKmsKeyManagerTest {
    // テスト対象
    private KeyManager sut;

    @BeforeEach
    void setUp() {
        // 初期化
        final KeyManagementConfigurationProperties configurationProperties = new KeyManagementConfigurationProperties();
        final KmsAsyncClient kmsAsyncClient = KmsAsyncClient.builder()//
                .region(Region.of(configurationProperties.getAwsKms().getRegion()))//
                .build();
        final ObjectStorageFileAccessor objectStorageFileAccessor = new ObjectStorageFileAccessorFake(
                "C:\\tmp\\objectstorage");
        sut = new AWSKmsKeyManager(kmsAsyncClient, objectStorageFileAccessor, configurationProperties);
    }

    /**
     * キーの作成のテスト
     */
    @Test
    void testCreateKeyPair() {
        final KeyInfo keyInfo = sut.createKey();
        assertNotNull(keyInfo);
        assertNotNull(keyInfo.getKeyId());
        assertNotNull(keyInfo.getState());
    }

    /**
     * キーの削除のテスト
     */
    @Test
    void testDeleteKeyPair() {
        final KeyInfo keyInfo = sut.createKey();
        assertNotNull(keyInfo);
        assertNotNull(keyInfo.getKeyId());
        assertNotNull(keyInfo.getState());
        final KeyInfo deleteKey = sut.deleteKey(keyInfo);
        assertNotNull(deleteKey);
        assertEquals(keyInfo.getKeyId(), deleteKey.getKeyId());
        assertEquals(KeyState.PENDING_DELETION.toString(), deleteKey.getState());
    }

    /**
     * キーの公開鍵の取得のテスト
     */
    @Test
    void testGetPublicKey() {
        // final KeyInfo keyInfo =
        // KeyInfo.builder().keyId("02399398-a986-4898-841f-96f382d8a809").build();

        final KeyInfo keyInfo = sut.createKey();
        assertNotNull(keyInfo);
        assertNotNull(keyInfo.getKeyId());
        assertNotNull(keyInfo.getState());

        final PublicKey publicKey = sut.getPublicKey(keyInfo);
        assertNotNull(publicKey);
        assertEquals("EC", publicKey.getAlgorithm());
        assertEquals("X.509", publicKey.getFormat());
        assertNotNull(publicKey.getEncoded());
    }

    /**
     * CSR（証明書署名要求）の作成のテスト
     */
    @Test
    void testCreateCsr() throws IOException {
        // final KeyInfo keyInfo =
        // KeyInfo.builder().keyId("02399398-a986-4898-841f-96f382d8a809").build();

        final KeyInfo keyInfo = sut.createKey();
        assertNotNull(keyInfo);
        assertNotNull(keyInfo.getKeyId());
        assertNotNull(keyInfo.getState());
        final String subject = "CN=www.example.co.jp, O=Example Corp, L=Minato Ctiy, ST=Tokyo, C=JP";
        final CertificateSigningRequest csr = sut.createCsr(keyInfo, subject);
        assertNotNull(csr);
        StringWriter stringWriter = new StringWriter();
        csr.exportPemTo(stringWriter);
        String pem = stringWriter.toString();
        System.out.println(pem);
        assertTrue(pem.contains("-----BEGIN CERTIFICATE REQUEST-----"));
        assertTrue(pem.contains("-----END CERTIFICATE REQUEST-----"));
    }

    /**
     * テスト用の自己署名証明書の作成のテスト
     */
    @Test
    void testCreateCertficate() throws IOException {
        // final KeyInfo keyInfo =
        // KeyInfo.builder().keyId("02399398-a986-4898-841f-96f382d8a809").build();
        final KeyInfo keyInfo = sut.createKey();
        assertNotNull(keyInfo);
        assertNotNull(keyInfo.getKeyId());
        assertNotNull(keyInfo.getState());
        final String subject = "CN=www.example.co.jp, O=Example Corp, L=Minato Ctiy, ST=Tokyo, C=JP";
        final CertificateSigningRequest csr = sut.createCsr(keyInfo, subject);
        assertNotNull(csr);
        final Certificate certificate = sut.createSelfSignedCertificate(csr, keyInfo);
        StringWriter stringWriter = new StringWriter();
        certificate.exportPemTo(stringWriter);
        String pem = stringWriter.toString();
        System.out.println(pem);
        assertTrue(pem.contains("-----BEGIN CERTIFICATE-----"));
        assertTrue(pem.contains("-----END CERTIFICATE-----"));
    }

}
