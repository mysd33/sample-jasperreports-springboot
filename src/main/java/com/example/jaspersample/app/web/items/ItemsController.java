package com.example.jaspersample.app.web.items;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.web.io.ResponseUtil;
import com.example.jaspersample.domain.message.MessageIds;
import com.example.jaspersample.domain.model.Item;
import com.example.jaspersample.domain.reports.ItemsReportCreator;
import com.example.jaspersample.domain.reports.ReportFile;
import com.example.jaspersample.domain.service.items.ItemsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 商品管理機能のController
 */
@Slf4j
@Controller
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemsController {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
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

    private ResponseEntity<Resource> createResponse(ReportFile reportFile) {
        InputStream is = reportFile.getInputStream();
        String fileName = reportFile.getFileName();
        long fileSize = reportFile.getFileSize();
        // レスポンスを返す
        try {
            return ResponseUtil.createResponseForPDF(is, fileName, fileSize);
        } catch (RuntimeException e) {
            try {
                is.close();
            } catch (IOException e1) {
                // 何もしない
                appLogger.warn(MessageIds.W_EX_8001, e1);
            }
            throw e;
        }
    }
}
