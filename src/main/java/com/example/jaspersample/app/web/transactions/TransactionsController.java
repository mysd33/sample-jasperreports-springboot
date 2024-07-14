package com.example.jaspersample.app.web.transactions;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.jaspersample.domain.service.transactions.TransactionsService;

import lombok.RequiredArgsConstructor;

/**
 * 取引管理機能のController
 */
@Controller
@RequestMapping("transactions")
@RequiredArgsConstructor
public class TransactionsController {
	private static final String REPORT_FILE_NAME = "transactions.pdf";
	private final TransactionsService transactionService;

	/**
	 * 取引一覧の帳票（PDF）を出力する
	 * 
	 * @return 帳票のPDFファイル	 * 
	 */
	@GetMapping("report")
	public ResponseEntity<Resource> getTransactionsReport() {
		// 取引一覧の帳票を取得する
		InputStream reportInputStream = transactionService.createTransactionsReport();
		Resource reportResource = new InputStreamResource(reportInputStream);

		// レスポンスを返す
		return ResponseEntity.ok()//
				// .contentType(MediaType.APPLICATION_OCTET_STREAM)//
				.contentType(MediaType.APPLICATION_PDF)//
				.header(HttpHeaders.CONTENT_DISPOSITION, //
						ContentDisposition.attachment()//
								.filename(REPORT_FILE_NAME)//
								.build().toString())//
				.body(reportResource);
	}
}
