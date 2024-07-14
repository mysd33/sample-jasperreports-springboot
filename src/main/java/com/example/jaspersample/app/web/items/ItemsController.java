package com.example.jaspersample.app.web.items;

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

import com.example.jaspersample.domain.service.items.ItemsService;

import lombok.RequiredArgsConstructor;

/**
 * 商品管理機能のController
 */
@Controller
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemsController {
	private static final String REPORT_FILE_NAME = "items.pdf";
	private final ItemsService itemsService;

	/**
	 * 商品一覧の帳票（PDF）を出力する
	 * 
	 * @return 帳票のPDFファイル
	 */
	@GetMapping("report")
	public ResponseEntity<Resource> getItemsReport() {
		// 商品一覧の帳票を取得する
		InputStream reportInputStream = itemsService.createItemsReport();
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
