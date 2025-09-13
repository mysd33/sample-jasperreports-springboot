package com.example.jaspersample;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.digitalsignature.config.DigitalSignatureConfigPackage;
import com.example.fw.common.keymanagement.config.KeyManagementConfigPackage;
import com.example.fw.common.logging.config.LoggingConfigPackage;
import com.example.fw.common.objectstorage.config.S3ConfigPackage;
import com.example.fw.common.reports.config.ReportsConfigPackage;

/**
 * インフラストラクチャ層の設定クラス
 */
@Configuration
// 帳票出力、鍵管理、PDF電子署名、オブジェクトストレージアクセス、ロギング拡張設定を追加
@ComponentScan(basePackageClasses = { //
        ReportsConfigPackage.class, KeyManagementConfigPackage.class, S3ConfigPackage.class,
        DigitalSignatureConfigPackage.class, LoggingConfigPackage.class })
public class InfraConfig {

}
