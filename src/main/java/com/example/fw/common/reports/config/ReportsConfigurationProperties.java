package com.example.fw.common.reports.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 帳票出力関連の設定を保持するプロパティクラス
 */
@Data
@ConfigurationProperties(prefix = "report")
public class ReportsConfigurationProperties {
	// コンパイル済の帳票様式を保存するデフォルトの一時ディレクトリ名
	private static final String DEFAULT_TEMP_JASPER_DIR = "jasper";

	/**
	 * コンパイル済の帳票様式を保存する一時ディレクトリのパス
	 */
	private String jasperFileTmpdir = DEFAULT_TEMP_JASPER_DIR; 

	
	// 帳票出力関連の設定があればここに追加する
}
