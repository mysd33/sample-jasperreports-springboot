package com.example.jasper.domain.reports;

import java.io.InputStream;
import java.util.List;

import com.example.jasper.domain.model.Item;

/**
 * 商品に関する帳票作成のインタフェース
 */
public interface ItemsReportCreator {

	/**
	 * 商品一覧の帳票を作成する
	 * 
	 * @param items 帳票に出力する商品リスト
	 * 
	 * @return 商品一覧帳票の入力ストリーム
	 */
	InputStream createItemListReport(List<Item> items);
}
