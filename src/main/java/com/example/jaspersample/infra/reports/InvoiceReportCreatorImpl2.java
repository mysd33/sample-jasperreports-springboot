package com.example.jaspersample.infra.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ResourceUtils;

import com.example.fw.common.reports.AbstractJasperReportCreator;
import com.example.fw.common.reports.PDFOptions;
import com.example.fw.common.reports.ReportCreator;
import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.model.OrderItem;
import com.example.jaspersample.domain.reports.InvoiceReportCreator;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * InvoiceReportCreatorの実装クラスその2<br>
 * 
 * 単項目も含めて、JRDataSourceを使用して帳票を作成する例
 */
// @ReportCreatorを付与し、Bean定義
@ReportCreator
// AbstractJasperReportCreatorを継承
// 型パラメータに帳票作成に必要なデータの型を指定
@RequiredArgsConstructor
public class InvoiceReportCreatorImpl2 extends AbstractJasperReportCreator<Order> implements InvoiceReportCreator {
	private static final String JRXML_FILE_PATH = "classpath:reports/invoice-report2.jrxml";
	// InvoiceReportDataMapperをDI
	private final InvoiceReportDataMapper mapper;

	// 業務APが定義する帳票出力処理
	@Override
	public InputStream createInvoice(Order order) {
		// PDFの読み取りパスワードのオプション設定例
		PDFOptions options = PDFOptions.builder()//
				.userPassword(order.getCustomer().getPdfPassword())//
				.build();
		// AbstractJasperReportCreatorが提供するcreatePDFReportメソッドをを呼び出すとPDF帳票作成する
		return createPDFReport(order, options);
	}

	// AbstractJasperReportCreatorのabstractメソッドgetJRXMLFileを実装して様式ファイルのパスを返す
	@Override
	protected File getJRXMLFile() throws FileNotFoundException {
		return ResourceUtils.getFile(JRXML_FILE_PATH);
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
			if (count == 0) {
				invoiceReportDataList.add(mapper.modelToReportHeaderAndItemData(data, item));
			} else {
				invoiceReportDataList.add(mapper.modelToReportItemData(item));
			}
			count++;
			*/
		}

		return new JRBeanCollectionDataSource(invoiceReportDataList);
	}

}
