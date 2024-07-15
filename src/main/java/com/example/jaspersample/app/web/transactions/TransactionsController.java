package com.example.jaspersample.app.web.transactions;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fw.web.io.ResponseUtil;
import com.example.jaspersample.domain.model.Transaction;
import com.example.jaspersample.domain.reports.TransactionsReportCreator;
import com.example.jaspersample.domain.service.transactions.TransactionsService;

import lombok.RequiredArgsConstructor;

/**
 * 取引管理機能のController
 */
@Controller
@RequestMapping("transactions")
@RequiredArgsConstructor
public class TransactionsController {
	//private static final String REPORT_FILE_NAME = "transactions.pdf";
	private static final String REPORT_FILE_NAME = "取引一覧.pdf";
	private final TransactionsService transactionService;
	private final TransactionsReportCreator reportCreator;

	/**
	 * 取引一覧の帳票（PDF）を出力する
	 * 
	 * @return 帳票のPDFファイル	 * 
	 */
	@GetMapping("report")
	public ResponseEntity<Resource> getTransactionsReport() {
		// 取引一覧の帳票を取得するサービスを実行し、帳票出力データを取得		
		List<Transaction> transactions = transactionService.findAll();
		// 取引一覧のPDF帳票の作成
		InputStream reportInputStream = reportCreator.createTransactionsReport(transactions);

		// レスポンスを返す
		return ResponseUtil.createResponseForPDF(reportInputStream, REPORT_FILE_NAME);
	}
}
