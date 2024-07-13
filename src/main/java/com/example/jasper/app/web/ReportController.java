package com.example.jasper.app.web;

import java.io.IOException;
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

import com.example.jasper.domain.service.items.ItemsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("reports")
@RequiredArgsConstructor
public class ReportController {
	private static final String REPORT_FILE_NAME = "items.pdf";
	private final ItemsService itemsService;

	/**
	 * 商品一覧の帳票（PDF）を出力する
	 * 
	 * @return 帳票のPDFファイル
	 * @throws IOException
	 */
	@GetMapping("items")
	public ResponseEntity<Resource> getItemsReport() throws IOException {
		// 商品一覧の帳票を取得する
		InputStream reportInputStream = itemsService.createItemsReport();
		Resource reportResource = new InputStreamResource(reportInputStream);

		// レスポンスを返す
		return ResponseEntity.ok()//
				.contentType(MediaType.APPLICATION_OCTET_STREAM)//
				.header(HttpHeaders.CONTENT_DISPOSITION, //
						ContentDisposition.attachment()//
								.filename(REPORT_FILE_NAME)//
								.build().toString())//
				.body(reportResource);
	}

}
