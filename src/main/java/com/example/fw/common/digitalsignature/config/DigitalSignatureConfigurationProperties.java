package com.example.fw.common.digitalsignature.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = DigitalSignatureConfigurationProperties.DIGITAL_SIGNATURE)
public class DigitalSignatureConfigurationProperties {
    static final String DIGITAL_SIGNATURE = "digitalsignature";
    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.ECDSA_SHA256;
    private boolean visible = false;
    // PKCS#12固有の設定(digitalsignature.pkcs12.*)
    private PKCS12Properties pkcs12 = new PKCS12Properties();
    // AWS KMS固有の設定(digitalsignature.aws-kms.*)
    private AWSKmsProperties awsKms = new AWSKmsProperties();

    /**
     * PKCS#12キーストア固有の設定を保持する内部クラス
     */
    @Data
    public static class PKCS12Properties {
        // キーストアファイルのパス
        private String keystoreFilePath = "";
        // キーストアのパスワード
        private String password = "";
    }

    /**
     * AWS KMS固有の設定を保持する内部クラス
     */
    @Data
    public static class AWSKmsProperties {
        // 署名に使用するAWS KMSのキーID
        private String keyId;
    }
}
