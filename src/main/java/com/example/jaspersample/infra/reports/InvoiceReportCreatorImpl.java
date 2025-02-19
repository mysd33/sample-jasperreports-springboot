package com.example.jaspersample.infra.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ResourceUtils;

import com.example.fw.common.reports.AbstractJasperReportCreator;
import com.example.fw.common.reports.PDFOptions;
import com.example.fw.common.reports.Report;
import com.example.fw.common.reports.ReportCreator;
import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.reports.InvoiceReportCreator;
import com.example.jaspersample.domain.reports.ReportFile;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * InvoiceReportCreatorの実装クラス<br>
 * 
 * 帳票出力のフレームワーク機能を利用して実装している
 */
// @ReportCreatorを付与し、Bean定義
@ReportCreator(id = "R003", name = "請求書")
// AbstractJasperReportCreatorを継承
// 型パラメータに帳票作成に必要なデータの型を指定
public class InvoiceReportCreatorImpl extends AbstractJasperReportCreator<Order> implements InvoiceReportCreator {
    private static final String INVOICE_FILE_NAME = "請求書.pdf";
    private static final String JRXML_FILE_PATH = "classpath:reports/invoice-report.jrxml";

    // 業務APが定義する帳票出力処理
    @Override
    public ReportFile createInvoice(Order order) {
        // PDFのセキュリティ設定のオプション例
        PDFOptions options = PDFOptions.builder()//
                // 読み取りパスワード
                .userPassword(order.getCustomer().getPdfPassword())//
                // 権限パスワード
                // .ownerPassword("admin")//
                // 特定の処理個別の暗号化レベル設定
                // .is128bitKey(false)
                // 特定の処理個別の権限設定
                // .permissionsDenied(List.of(
                // PdfPermissionsEnum.COPY,
                // PdfPermissionsEnum.PRINTING,
                // PdfPermissionsEnum.MODIFY_CONTENTS
                // ))//
                .build();
        // AbstractJasperReportCreatorが提供するcreatePDFReportメソッドをを呼び出すとPDF帳票作成する
        Report report = createPDFReport(order, options);
        return ReportFile.builder()//
                .inputStream(report.getInputStream())//
                .fileName(INVOICE_FILE_NAME)//
                .fileSize(report.getSize())//
                .build();
    }

    // AbstractJasperReportCreatorのabstractメソッドgetJRXMLFileを実装して様式ファイルのパスを返す
    @Override
    protected File getMainJRXMLFile() throws FileNotFoundException {
        return ResourceUtils.getFile(JRXML_FILE_PATH);
    }

    // AbstractJasperReportCreatorのメソッドgetParametersを実装して、帳票作成に必要なパラメータを返す
    @Override
    protected Map<String, Object> getParameters(Order data) {
        // 帳票の鏡部分のデータをパラメータとして設定した例
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orderId", data.getId());
        parameters.put("customerZip", data.getCustomer().getZip());
        parameters.put("customerAddress", data.getCustomer().getAddress());
        parameters.put("customerName", data.getCustomer().getName());
        parameters.put("billingSourceName", data.getBillingSource().getName());
        parameters.put("billingSourceZip", data.getBillingSource().getZip());
        parameters.put("billingSourceAddress", data.getBillingSource().getAddress());
        parameters.put("billingSourceTel", data.getBillingSource().getTel());
        parameters.put("billingSourceManager", data.getBillingSource().getManager());
        return parameters;
    }

    // AbstractJasperReportCreatorのabstractメソッドgetDataSourceを実装して、データソースを返す
    @Override
    protected JRDataSource getDataSource(Order data) {
        // 帳票の一覧部分に出力する注文明細のデータを設定した例
        return new JRBeanCollectionDataSource(data.getOrderItems());
    }

}
