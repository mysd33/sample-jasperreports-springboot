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
import com.example.jaspersample.domain.reports.BillingReportCreator;
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
	private final BillingReportCreator reportCreator;

	/**
	 * 請求書（PDF）を出力する
	 * 
	 * @return 請求書のPDFファイル
	 */
	@GetMapping("invoice/{orderId}")
	public ResponseEntity<Resource> getInvoice(@PathVariable String orderId) {
		// 注文情報の取得の取得
		Order order = orderService.findOne(orderId);
		// 請求書の作成
		InputStream reportInputStream = reportCreator.createInvoice(order);

		// レスポンスを返す
		return ResponseUtil.createResponseForPDF(reportInputStream, INVOICE_FILE_NAME);
	}

}
