package com.example.jaspersample.infra.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.util.ResourceUtils;

import com.example.fw.common.exception.SystemException;
import com.example.fw.common.reports.AbstractJasperReportCreator;
import com.example.fw.common.reports.PDFOptions;
import com.example.fw.common.reports.ReportCreator;
import com.example.jaspersample.domain.message.MessageIds;
import com.example.jaspersample.domain.reports.InvoiceReportCSVData;
import com.example.jaspersample.domain.reports.InvoiceReportCreatorForCSV;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

/**
 * InvoiceReportCreatorの実装クラスその3<br>
 * 
 * 単項目も含めて、JRDataSourceを使用して帳票を作成する例
 * 帳票データがCSVファイルの場合
 */
// @ReportCreatorを付与し、Bean定義
@ReportCreator
// AbstractJasperReportCreatorを継承
// 型パラメータに帳票作成に必要なデータの型を指定
public class InvoiceReportCreatorForCSVImpl extends AbstractJasperReportCreator<InvoiceReportCSVData> implements InvoiceReportCreatorForCSV {
	private static final String JRXML_FILE_PATH = "classpath:reports/invoice-report2.jrxml";
	
	// 業務APが定義する帳票出力処理
	@Override
	public InputStream createInvoice(InvoiceReportCSVData csvData) {
		// PDFの読み取りパスワードのオプション設定例
		PDFOptions options = PDFOptions.builder()//
				.userPassword(csvData.getPdfPassword())//
				.build();
		// AbstractJasperReportCreatorが提供するcreatePDFReportメソッドをを呼び出すとPDF帳票作成する
		return createPDFReport(csvData, options);
	}

	// AbstractJasperReportCreatorのabstractメソッドgetJRXMLFileを実装して様式ファイルのパスを返す
	@Override
	protected File getJRXMLFile() throws FileNotFoundException {
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
		} catch (Exception e) {			
			throw new SystemException(e, MessageIds.E_EX_9001);
		}			
	}

}
