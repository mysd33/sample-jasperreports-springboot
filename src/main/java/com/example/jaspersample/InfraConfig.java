package com.example.jaspersample;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.reports.config.ReportsConfigPackage;

/**
 * インフラストラクチャ層の設定クラス
 */
@Configuration
// 帳票出力機能の設定情報を追加
@ComponentScan(basePackageClasses = ReportsConfigPackage.class)
public class InfraConfig {

}
