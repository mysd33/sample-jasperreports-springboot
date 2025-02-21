package com.example.jaspersample.app.web.transactions;

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
import com.example.jaspersample.domain.model.Transaction;
import com.example.jaspersample.domain.reports.ReportFile;
import com.example.jaspersample.domain.reports.TransactionsReportCreator;
import com.example.jaspersample.domain.service.transactions.TransactionsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取引管理機能のController
 */
@Slf4j
@Controller
@RequestMapping("transactions")
@RequiredArgsConstructor
public class TransactionsController {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final TransactionsService transactionService;
    private final TransactionsReportCreator reportCreator;

    /**
     * 取引一覧の帳票（PDF）を出力する
     * 
     * @return 帳票のPDFファイル
     */
    @GetMapping("report")
    public ResponseEntity<Resource> getTransactionsReport() {
        // 取引一覧の帳票を取得するサービスを実行し、帳票出力データを取得
        List<Transaction> transactions = transactionService.findAll();
        // 取引一覧のPDF帳票の作成
        ReportFile reportFile = reportCreator.createTransactionsReport(transactions);

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
