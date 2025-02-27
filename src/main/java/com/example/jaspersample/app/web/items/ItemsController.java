package com.example.jaspersample.app.web.items;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fw.web.io.ResponseUtil;
import com.example.jaspersample.domain.model.Item;
import com.example.jaspersample.domain.reports.ItemsReportCreator;
import com.example.jaspersample.domain.reports.ReportFile;
import com.example.jaspersample.domain.service.items.ItemsService;

import lombok.RequiredArgsConstructor;

/**
 * 商品管理機能のController
 */
@Controller
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemsController {
    private final ItemsService itemsService;
    private final ItemsReportCreator reportCreator;

    /**
     * 商品一覧の帳票（PDF）を出力する
     * 
     * @return 帳票のPDFファイル
     */
    @GetMapping("report")
    public ResponseEntity<Resource> getItemsReport() {
        // 商品一覧を取得するサービスを実行し、帳票出力データを取得
        List<Item> items = itemsService.findAll();
        // 商品一覧のPDF帳票の作成
        ReportFile reportFile = reportCreator.createItemsReport(items);

        // レスポンスを返す
        return createResponse(reportFile);
    }

    /**
     * レスポンスを作成する
     * 
     * @param reportFile 帳票ファイル
     * @return レスポンス
     */
    private ResponseEntity<Resource> createResponse(ReportFile reportFile) {
        return ResponseUtil.createResponseForPDF(reportFile.getInputStream(), reportFile.getFileName(),
                reportFile.getFileSize());
    }
}
