package com.example.jaspersample.infra.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.springframework.util.ResourceUtils;

import com.example.fw.common.exception.SystemException;
import com.example.fw.common.reports.AbstractJasperReportCreator;
import com.example.fw.common.reports.PDFOptions;
import com.example.fw.common.reports.Report;
import com.example.fw.common.reports.ReportCreator;
import com.example.jaspersample.domain.message.MessageIds;
import com.example.jaspersample.domain.reports.InvoiceReportCSVData;
import com.example.jaspersample.domain.reports.InvoiceReportCreatorForCSV;
import com.example.jaspersample.domain.reports.ReportFile;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

/**
 * InvoiceReportCreatorの実装クラスその3<br>
 * 
 * 単項目も含めて、JRDataSourceを使用して帳票を作成する例 帳票データがCSVファイルの場合
 */
// @ReportCreatorを付与し、Bean定義
@ReportCreator(id = "R003", name = "請求書")
// AbstractJasperReportCreatorを継承
// 型パラメータに帳票作成に必要なデータの型を指定
public class InvoiceReportCreatorForCSVImpl extends AbstractJasperReportCreator<InvoiceReportCSVData>
        implements InvoiceReportCreatorForCSV {
    private static final String INVOICE_FILE_NAME = "請求書.pdf";
    private static final String JRXML_FILE_PATH = "classpath:reports/invoice-report2.jrxml";

    // 業務APが定義する帳票出力処理
    @Override
    public ReportFile createInvoice(InvoiceReportCSVData csvData) {
        // PDFのセキュリティ設定のオプション例
        PDFOptions options = PDFOptions.builder()//
                // 読み取りパスワード
                .userPassword(csvData.getPdfPassword())//
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
        Report report = createPDFReport(csvData, options);
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

    // AbstractJasperReportCreatorのabstractメソッドgetDataSourceを実装して、データソースを返す
    @Override
    protected JRDataSource getDataSource(InvoiceReportCSVData data) {
        try {
            // CSVファイルからJRDataSourceを生成
            JRCsvDataSource dataSource = new JRCsvDataSource(data.getInputStream(), "UTF-8");
            // 1行目をフィールドを表すカラムヘッダーとして使用する設定
            dataSource.setUseFirstRowAsHeader(true);
            return dataSource;
        } catch (UnsupportedEncodingException e) {
            throw new SystemException(e, MessageIds.E_EX_9001);
        }
    }

}
