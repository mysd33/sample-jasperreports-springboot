package com.example.fw.common.keymanagment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final String keyAlias = "sign-test-ecdsa";
        final KeyInfo keyInfo = sut.createKey(keyAlias);
        assertNotNull(keyInfo);
        assertNotNull(keyInfo.getKeyId());
        assertNotNull(keyInfo.getState());
        sut.deleteKeyAlias(keyAlias);
        sut.deleteKey(keyInfo);
    }

    /**
     * キーのエイリアス追加のテスト
     */
    @Test
    void testAddKeyAlias() {
        final String keyAlias = "sign-test-ecdsa";
        final KeyInfo keyInfo = sut.createKey();
        assertNotNull(keyInfo);
        assertNotNull(keyInfo.getKeyId());
        assertNotNull(keyInfo.getState());
        sut.addKeyAlias(keyInfo, keyAlias);
        sut.deleteKeyAlias(keyAlias);
        sut.deleteKey(keyInfo);
    }

    /**
     * キーのエイリアスからの検索のテスト
     */
    @Test
    void testFindKeyByAlias01() {
        final String keyAlias = "sign-test-ecdsa";
        sut.deleteKeyAlias(keyAlias);
        final KeyInfo createdKeyInfo = sut.createKey(keyAlias);
        assertNotNull(createdKeyInfo);
        assertNotNull(createdKeyInfo.getKeyId());
        assertNotNull(createdKeyInfo.getState());
        final KeyInfo foundKeyInfo = sut.findKeyByAlias(keyAlias);
        assertNotNull(foundKeyInfo);
        assertEquals(createdKeyInfo.getKeyId(), foundKeyInfo.getKeyId());
        assertEquals(createdKeyInfo.getState(), foundKeyInfo.getState());
        sut.deleteKeyAlias(keyAlias);
        sut.deleteKey(createdKeyInfo);
    }

    /**
     * キーのエイリアスが存在しない場合での検索のテスト
     */
    @Test
    void testFindKeyByAlias02() {
        final String keyAlias = "sign-test-ecdsa";
        sut.deleteKeyAlias(keyAlias);
        final KeyInfo actual = sut.findKeyByAlias(keyAlias);
        assertNull(actual);
    }

    /**
     * キーの削除のテスト
     */
    @Test
    void testDeleteKeyPair() {
        final String keyAlias = "sign-test-ecdsa";
        final KeyInfo keyInfo = sut.createKey(keyAlias);
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

        final String keyAlias = "sign-test-ecdsa";
        final KeyInfo keyInfo = sut.createKey(keyAlias);
        assertNotNull(keyInfo);
        assertNotNull(keyInfo.getKeyId());
        assertNotNull(keyInfo.getState());

        final PublicKey publicKey = sut.getPublicKey(keyInfo);
        assertNotNull(publicKey);
        assertEquals("EC", publicKey.getAlgorithm());
        assertEquals("X.509", publicKey.getFormat());
        assertNotNull(publicKey.getEncoded());
        sut.deleteKeyAlias(keyAlias);
        sut.deleteKey(keyInfo);
    }

    /**
     * CSR（証明書署名要求）の作成のテスト
     */
    @Test
    void testCreateCsr() throws IOException {
        // final KeyInfo keyInfo =
        // KeyInfo.builder().keyId("02399398-a986-4898-841f-96f382d8a809").build();

        final String keyAlias = "sign-test-ecdsa";
        final KeyInfo keyInfo = sut.createKey(keyAlias);
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
        sut.deleteKeyAlias(keyAlias);
        sut.deleteKey(keyInfo);
    }

    /**
     * テスト用の自己署名証明書の作成のテスト
     */
    @Test
    void testCreateCertficate() throws IOException {
        // final KeyInfo keyInfo =
        // KeyInfo.builder().keyId("02399398-a986-4898-841f-96f382d8a809").build();
        final String keyAlias = "sign-test-ecdsa";
        final KeyInfo keyInfo = sut.createKey(keyAlias);
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
        sut.deleteKeyAlias(keyAlias);
        sut.deleteKey(keyInfo);
    }

}
