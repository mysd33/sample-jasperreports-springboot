package com.example.fw.common.digitalsignature.config;

import java.io.File;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.digitalsignature.AWSKmsPAdESReportSigner;
import com.example.fw.common.digitalsignature.PKCS12BasicReportSigner;
import com.example.fw.common.digitalsignature.PKCS12PAdESReportSiginer;
import com.example.fw.common.digitalsignature.ReportSigner;
import com.example.fw.common.digitalsignature.SignatureOptions;
import com.example.fw.common.keymanagement.KeyManager;
import com.example.fw.common.objectstorage.ObjectStorageFileAccessor;
import com.example.fw.common.reports.config.ReportsConfigurationProperties;

import lombok.RequiredArgsConstructor;

/**
 * 電子署名に関する設定を定義するクラス
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DigitalSignatureConfigurationProperties.class)
public class DigitalSignatureConfig {
    private static final String DIGITAL_SIGNATURE_TYPE = //
            DigitalSignatureConfigurationProperties.DIGITAL_SIGNATURE + ".type";
    private final DigitalSignatureConfigurationProperties digitalSignatureConfigurationProperties;
    private final ReportsConfigurationProperties reportsConfigurationProperties;

    /**
     * PKCS#12ファイルを使用し、通常のPDF署名を付与するReportSignerのBean定義
     */
    @Bean
    @ConditionalOnProperty(name = DIGITAL_SIGNATURE_TYPE, havingValue = "pkcs12-basic")
    ReportSigner reportSignerByPKCS12Basic() {
        return new PKCS12BasicReportSigner(reportsConfigurationProperties, //
                SignatureOptions.builder()//
                        .keyStoreFile(
                                new File(digitalSignatureConfigurationProperties.getPkcs12().getKeystoreFilePath()))//
                        .password(digitalSignatureConfigurationProperties.getPkcs12().getPassword())//
                        .visible(digitalSignatureConfigurationProperties.isVisible())//
                        .stampImagePath(digitalSignatureConfigurationProperties.getStampImagePath())//
                        .build());
    }

    /**
     * PKCS#12ファイルを使用し、PAdES形式でのPDF署名を付与するReportSignerのBean定義 （デフォルト）
     */
    @Bean
    @ConditionalOnProperty(name = DIGITAL_SIGNATURE_TYPE, havingValue = "pkcs12-pades")    
    ReportSigner reportSignerByPKCS12() {
        return new PKCS12PAdESReportSiginer(reportsConfigurationProperties, //
                SignatureOptions.builder()//
                        .keyStoreFile(
                                new File(digitalSignatureConfigurationProperties.getPkcs12().getKeystoreFilePath()))//
                        .password(digitalSignatureConfigurationProperties.getPkcs12().getPassword())//
                        // 可視署名は未対応
                        //.visible(digitalSignatureConfigurationProperties.isVisible())//
                        .build());
    }

    /**
     * AWS KMSによる署名鍵を使用し、PAdES形式でのPDF署名を付与するReportSignerのBean定義
     * 
     * @param kmsAsyncClient          KMS非同期クライアント
     * @param configurationProperties 鍵管理の設定プロパティ
     * @return ReportSignerのインスタンス
     */
    @Bean
    @ConditionalOnProperty(name = DIGITAL_SIGNATURE_TYPE, havingValue = "aws-kms-pades")
    ReportSigner reportSignerByKms(KeyManager keyManager, ObjectStorageFileAccessor objectStorageFileAccessor) {
        // 可視署名は未対応
        return new AWSKmsPAdESReportSigner(keyManager, //
                digitalSignatureConfigurationProperties, //
                reportsConfigurationProperties);
    }

}
