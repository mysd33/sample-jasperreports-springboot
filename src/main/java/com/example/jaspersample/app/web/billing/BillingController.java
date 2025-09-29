package com.example.jaspersample.app.web.billing;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fw.web.io.ResponseUtil;
import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.reports.InvoiceReportCSVData;
import com.example.jaspersample.domain.reports.InvoiceReportCreator;
import com.example.jaspersample.domain.reports.ReportFile;
import com.example.jaspersample.domain.service.order.OrderService;
import com.example.jaspersample.infra.reports.InvoiceReportCreatorForCSVImpl;

import lombok.RequiredArgsConstructor;

/**
 * 商品管理機能のController
 */

@Controller
@RequestMapping("billing")
@RequiredArgsConstructor
public class BillingController {
    private final OrderService orderService;
    private final InvoiceReportCreator invoiceReportCreatorImpl;
    private final InvoiceReportCreator invoiceReportCreatorImpl2;
    private final InvoiceReportCreatorForCSVImpl invoiceReportCreatorForCSV;
    private final InvoiceReportCreator invoiceReportCreatorWithSign;

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
        ReportFile reportFile = invoiceReportCreatorImpl.createInvoice(order);
        return createResponse(reportFile);
    }

    /**
     * 請求書（PDF）を出力する その2
     * 
     * 請求書の様式が、単項目含めてすべてJRDataSourceから取得する版
     * 
     * @return 請求書のPDFファイル
     * @throws IOException
     */
    @GetMapping("invoice2/{orderId}")
    public ResponseEntity<Resource> getInvoice2(@PathVariable String orderId) throws IOException {
        // 注文情報の取得の取得
        Order order = orderService.findOne(orderId);
        // 請求書の作成
        ReportFile reportFile = invoiceReportCreatorImpl2.createInvoice(order);
        return createResponse(reportFile);
    }

    /**
     * 請求書（PDF）を出力する その3
     * 
     * 請求書の様式が、単項目含めてすべてJRDataSourceから取得する版<br/>
     * CSVファイルを入力として、帳票出力する。
     * 
     * @return 請求書のPDFファイル
     * @throws IOException
     */
    @GetMapping("invoice3/{orderId}")
    public ResponseEntity<Resource> getInvoice3(@PathVariable String orderId) {
        // 注文情報のCSVデータの取得
        InvoiceReportCSVData csvData = orderService.getReportCSVData(orderId);

        // CSVデータより請求書の作成
        ReportFile reportFile = invoiceReportCreatorForCSV.createInvoice(csvData);
        return createResponse(reportFile);
    }

    /**
     * 請求書（PDF）を出力する その4
     * 
     * 請求書の様式が、単項目含めてすべてJRDataSourceから取得する版<br/>
     * PDF電子署名ありの例
     * 
     * @return 請求書のPDFファイル
     * @throws IOException
     */
    @GetMapping("invoice4/{orderId}")
    public ResponseEntity<Resource> getInvoice4(@PathVariable String orderId) {
        // 注文情報の取得の取得
        Order order = orderService.findOne(orderId);
        // 請求書の作成
        ReportFile reportFile = invoiceReportCreatorWithSign.createInvoice(order);
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