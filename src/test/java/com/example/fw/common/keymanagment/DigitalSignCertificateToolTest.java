package com.example.fw.common.keymanagment;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import com.example.fw.common.keymanagement.Certificate;
import com.example.fw.common.keymanagement.CertificateSigningRequest;
import com.example.fw.common.keymanagement.KeyInfo;
import com.example.fw.common.keymanagement.KeyManager;
import com.example.fw.common.keymanagement.config.KeyManagementConfigurationProperties;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 電子署名関連のツールクラス。<br>
 * 
 * KMSを使って電子署名のための暗号鍵を作成し、公開鍵をもとにCSR（証明書署名要求）およびテスト用に自己署名証明書を生成する。<br>
 */
@Slf4j
@Profile({ "dev", "log_default" })
//  簡単のためSpringBootTestを使ってツール作成するが、アプリケーションとして実装するとよい
@SpringBootTest(classes = DigitalSignCertificateToolTestConfig.class)
class DigitalSignCertificateToolTest {
    private static final String CERTS_LOCAL_DIR = "certs/kms/";
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    @Autowired
    private KeyManager keyManager;
    @Autowired
    private KeyManagementConfigurationProperties keyManagementConfigurationProperties;

    @Test
    void createKeyAndCertificate() throws IOException {
        final String certsCsrFileName = keyManagementConfigurationProperties.getCsrPemFileName();
        final String selfSignedCertificateFileName = keyManagementConfigurationProperties
                .getSelfSignedCertPemFileName();
        // サブジェクト情報
        // 認証局から指定されているCSR・証明書の作成に必要な項目を指定すること
        // 参考：https://www.cybertrust.co.jp/ssl/support/csr.html
        final String subject = "CN=www.example.co.jp, O=Example Corp, L=Minato Ctiy, ST=Tokyo, C=JP";

        // 暗号鍵の作成
        final KeyInfo keyInfo = keyManager.createKey();
        // 既存のキーを使う場合
        // final String existingKeyId = "a9d52165-8223-4c95-9ec3-32e1e6fd2bee";
        // final KeyInfo keyInfo = KeyInfo.builder().keyId(existingKeyId).build();
        appLogger.debug("作成したKey ID: {}", keyInfo.getKeyId());

        // CSRの作成
        final CertificateSigningRequest csr = keyManager.createCsr(keyInfo, subject);
        // CSRをオブジェクトストレージに保存
        keyManager.saveCsrToObjectStorage(csr, keyInfo);

        // 自己署名証明書の作成
        final Certificate certificate = keyManager.createSelfSignedCertificate(csr, keyInfo);
        // 自己署名証明書をオブジェクトストレージに保存
        keyManager.saveSelfSignedCertificateToObjectStorage(certificate, keyInfo);

        // 参考のため、同じものをローカルディレクトリにもファイル保存
        // ディレクトリを作成していない場合は作成する
        String certsBaseDirStr = CERTS_LOCAL_DIR + keyInfo.getKeyId();
        File certsBaseDir = new File(certsBaseDirStr);
        if (!certsBaseDir.exists()) {
            certsBaseDir.mkdirs();
        }
        csr.exportPemTo(certsBaseDirStr + "/" + certsCsrFileName);
        certificate.exportPemTo(certsBaseDirStr + "/" + selfSignedCertificateFileName);

    }

}
