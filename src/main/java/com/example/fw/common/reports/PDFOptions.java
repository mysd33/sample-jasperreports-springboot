package com.example.fw.common.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PDF出力時のオプション設定を行うクラス
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PDFOptions {
	// 読み取りパスワード
	private String userPassword;
	// 権限パスワード
	private String ownerPassword;

	// TODO: PDFセキュリティ関連の設定（コピー許可、編集許可、印刷許可・・・）
	// TODO: 権限設定を分かりやすいAPIに変更する
	/*
	 * public static final Integer ALL_PERMISSIONS = PdfWriter.ALLOW_ASSEMBLY |
	 * PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_DEGRADED_PRINTING |
	 * PdfWriter.ALLOW_FILL_IN | PdfWriter.ALLOW_MODIFY_ANNOTATIONS |
	 * PdfWriter.ALLOW_MODIFY_CONTENTS | PdfWriter.ALLOW_PRINTING |
	 * PdfWriter.ALLOW_SCREENREADERS;
	 */
	private Integer permissions;

	/**
	 * PDFオプションの設定があるかどうか
	 * 
	 * @return PDFオプション指定がある場合はtrue
	 */
	public boolean hasOptions() {
		return userPassword != null || ownerPassword != null || permissions != null;
	}

	/**
	 * PDFの暗号化設定が必要かどうかを返却します
	 * 
	 * @return trueの場合は暗号化設定が必要
	 */
	public boolean isEncrypted() {
		return (userPassword != null && !userPassword.isEmpty()) || (ownerPassword != null && !ownerPassword.isEmpty());
	}
	
	/**
	 * PDFの権限設定が必要かどうかを返却します
	 * 
	 * @return trueの場合は権限設定が必要
	 */
	public boolean hasPermissions() {
		return permissions != null;			
	}
}
