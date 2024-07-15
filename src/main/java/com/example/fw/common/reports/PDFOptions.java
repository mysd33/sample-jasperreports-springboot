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

	// TODO: 必要に応じてPDFセキュリティ関連の設定を追加

	/**
	 * PDFの暗号化設定が必要かどうかを返却します
	 * 
	 * @return trueの場合は暗号化設定が必要
	 */
	public boolean isEncrypted() {
		return (userPassword != null && !userPassword.isEmpty())
				|| (ownerPassword != null && !ownerPassword.isEmpty());
	}
}
