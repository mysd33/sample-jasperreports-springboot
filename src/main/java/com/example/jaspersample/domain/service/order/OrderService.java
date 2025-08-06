package com.example.jaspersample.domain.service.order;

import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.reports.InvoiceReportCSVData;

/**
 * 注文サービスインタフェース
 */
public interface OrderService {
	/**
	 * 注文情報を取得する
	 * 
	 * @param id 注文ID
	 * @return 注文情報
	 */
	Order findOne(String id);
	
	/**
	 * 注文情報のCSVファイルを取得する
	 * 
	 * @param id 注文ID
	 * @return 注文情報のCSVファイル
	 */
	InvoiceReportCSVData getReportCSVData(String id);
}
