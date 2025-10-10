package com.example.jaspersample.infra.reports;

import java.util.ArrayList;
import java.util.List;

import com.example.fw.common.reports.AbstractJasperReportCreator;
import com.example.fw.common.reports.PDFOptions;
import com.example.fw.common.reports.Report;
import com.example.fw.common.reports.ReportCreator;
import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.model.OrderItem;
import com.example.jaspersample.domain.reports.InvoiceReportCreator;
import com.example.jaspersample.domain.reports.ReportFile;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * InvoiceReportCreatorの実装クラスその2<br>
 * 
 * 単項目も含めて、JRDataSourceを使用して帳票を作成する例
 */
// @ReportCreatorを付与し、Bean定義
@ReportCreator(id = "R003", name = "請求書")
// AbstractJasperReportCreatorを継承
// 型パラメータに帳票作成に必要なデータの型を指定
@RequiredArgsConstructor
public class InvoiceReportCreatorImpl2 extends AbstractJasperReportCreator<Order> implements InvoiceReportCreator {
    private static final String INVOICE_FILE_NAME = "請求書.pdf";
    private static final String JRXML_FILE_PATH = "reports/invoice-report2.jrxml";
    // InvoiceReportDataMapperをDI
    private final InvoiceReportDataMapper mapper;

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
    protected String getMainJRXMLFile() {
        return JRXML_FILE_PATH;
    }

    // AbstractJasperReportCreatorのabstractメソッドgetDataSourceを実装して、データソースを返す
    @Override
    protected JRDataSource getDataSource(Order data) {
        List<InvoiceReportData> invoiceReportDataList = new ArrayList<>();
        // int count = 0;
        // 帳票の一覧部分に出力する注文明細のデータを設定した例

        for (OrderItem item : data.getOrderItems()) {
            // 帳票出力用のデータにコピー
            invoiceReportDataList.add(mapper.modelToReportData(data, item));

            // 明細の一行目だけにヘッダーデータがコピーされる実装でもOK
            /*
             * if (count == 0) {
             * invoiceReportDataList.add(mapper.modelToReportHeaderAndItemData(data, item));
             * } else { invoiceReportDataList.add(mapper.modelToReportItemData(item)); }
             * count++;
             */
        }

        return new JRBeanCollectionDataSource(invoiceReportDataList);
    }

}
