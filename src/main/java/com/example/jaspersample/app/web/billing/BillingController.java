package com.example.jaspersample.app.web.billing;

import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fw.web.io.ResponseUtil;
import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.reports.InvoiceReportCreator;
import com.example.jaspersample.domain.service.order.OrderService;

import lombok.RequiredArgsConstructor;

/**
 * 商品管理機能のController
 */
@Controller
@RequestMapping("billing")
@RequiredArgsConstructor
public class BillingController {
	private static final String INVOICE_FILE_NAME = "請求書.pdf";
	private final OrderService orderService;
	private final InvoiceReportCreator invoiceReportCreatorImpl;
	private final InvoiceReportCreator invoiceReportCreatorImpl2;

	/**
	 * 請求書（PDF）を出力する
	 * 
	 * 請求書の様式が、単項目はパラメータ、繰り返し項目はJRDataSourceから取得する版
	 * 
	 * @return 請求書のPDFファイル
	 */
	@GetMapping("invoice/{orderId}")
	public ResponseEntity<Resource> getInvoice(@PathVariable String orderId) {
		// 注文情報の取得の取得
		Order order = orderService.findOne(orderId);
		// 請求書の作成
		InputStream reportInputStream = invoiceReportCreatorImpl.createInvoice(order);

		// レスポンスを返す
		return ResponseUtil.createResponseForPDF(reportInputStream, INVOICE_FILE_NAME);
	}

	/**
	 * 請求書（PDF）を出力する　その2
	 * 
	 * 請求書の様式が、単項目含めてすべてJRDataSourceから取得する版
	 * 
	 * @return 請求書のPDFファイル
	 */
	@GetMapping("invoice2/{orderId}")
	public ResponseEntity<Resource> getInvoice2(@PathVariable String orderId) {
		// 注文情報の取得の取得
		Order order = orderService.findOne(orderId);
		// 請求書の作成
		InputStream reportInputStream = invoiceReportCreatorImpl2.createInvoice(order);

		// レスポンスを返す
		return ResponseUtil.createResponseForPDF(reportInputStream, INVOICE_FILE_NAME);
	}
}
