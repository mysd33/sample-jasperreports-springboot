package com.example.fw.common.reports;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.fw.common.exception.SystemException;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;
import com.example.fw.common.message.CommonFrameworkMessageIds;
import com.example.fw.common.reports.config.ReportsConfigurationProperties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;

// TODO: SubReportの対応を検討

/**
 * Jasper Reportsを使用して、帳票作成のための基底クラス
 * 
 * @param <T> 帳票データの型
 */
@Slf4j
public abstract class AbstractJasperReportCreator<T> {
	private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
	private static final MonitoringLogger monitoringLogger = LoggerFactory.getMonitoringLogger(log);
	private ReportsConfigurationProperties config;
	private Path jasperPath;

	@Autowired
	public void setConfig(final ReportsConfigurationProperties config) {
		this.config = config;
	}

	@PostConstruct
	public void init() throws FileNotFoundException, JRException {
		// コンパイル済の帳票様式を保存する一時ディレクトリを作成する
		String tempDir = System.getProperty("java.io.tmpdir");
		jasperPath = Path.of(tempDir, config.getJasperFileTmpdir());
		// 一時ディレクトリが存在しない場合は作成する
		jasperPath.toFile().mkdirs();
		appLogger.debug("jasperPath: {}", jasperPath);
		// あらかじめ帳票様式ファイルをコンパイルする
		try {
			compileJRXML();
		} catch (FileNotFoundException | JRException e) {
			monitoringLogger.error(CommonFrameworkMessageIds.E_CM_FW_9003, e);
			throw e;
		}

	}

	@PreDestroy
	public void destroy() throws FileNotFoundException {
		// jasperファイルを削除する
		File jasperFile = getJasperFile();
		jasperFile.delete();
	}

	/**
	 * 帳票を作成する
	 * 
	 * @param data 帳票データ
	 * @return PDFファイルのInputStreamデータ
	 */
	public InputStream createPDFReport(final T data) {
		return createPDFReport(data, PDFOptions.builder().build());
	}

	/**
	 * 帳票を作成する
	 * 
	 * @param data    帳票データ
	 * @param options PDF出力時のオプション設定
	 * @return PDFファイルのInputStreamデータ
	 */
	public InputStream createPDFReport(final T data, final PDFOptions options) {
		Map<String, Object> parameters = getParameters(data);
		JRDataSource dataSource = getDataSource(data);
		JasperReport jasperReport = null;
		try {
			File jasperFile = getJasperFile();
			// コンパイル済の帳票様式を読み込む
			jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
		} catch (FileNotFoundException | JRException e) {
			throw new SystemException(e, CommonFrameworkMessageIds.E_CM_FW_9004);
		}

		try {
			// 帳票様式に帳票データを渡して、帳票を作成する
			// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperFillManager.html
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			// PDF形式で出力
			// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperExportManager.html

			// そのままバイト配列に出力する実装例
			return exportPDF(jasperPrint, options);
		} catch (JRException e) { // | IOException e) {
			throw new SystemException(e, CommonFrameworkMessageIds.E_CM_FW_9005);
		}
	}

	/**
	 * 帳票様式ファイル(jrxmlファイル)を取得する
	 * 
	 * @return 帳票様式ファイル
	 * @throws FileNotFoundException
	 */
	protected abstract File getJRXMLFile() throws FileNotFoundException;

	/**
	 * 帳票作成に必要なパラメータを取得する
	 * 
	 * @param data 帳票データ
	 * @return パラメータ
	 */
	protected Map<String, Object> getParameters(final T data) {
		return new HashMap<String, Object>();
	}

	/**
	 * 帳票作成に必要なデータソースを取得する
	 * 
	 * @param data 帳票データ
	 * @return データソース
	 */
	protected abstract JRDataSource getDataSource(final T data);

	/**
	 * コンパイル済の帳票様式ファイル(jasperファイル)を取得する
	 * 
	 * @return コンパイル済の帳票様式ファイル
	 * @throws FileNotFoundException
	 */
	protected File getJasperFile() throws FileNotFoundException {
		File jrxmlFile = getJRXMLFile();
		String jasperFileName = jrxmlFile.getName().replace(".jrxml", ".jasper");
		// 一時フォルダにあるファイルパスを返却
		return jasperPath.resolve(jasperFileName).toFile();
	}

	/**
	 * jrxmlの帳票様式ファイルをコンパイルする
	 * 
	 * @return コンパイル済の帳票様式
	 * @throws JRException           様式のコンパイルエラーまたはコンパイル済の帳票の保存時のエラー
	 * @throws FileNotFoundException jrxmlファイルが見つからない場合
	 */
	private JasperReport compileJRXML() throws JRException, FileNotFoundException {
		// TODO: JDK21の場合は、帳票コンパイル時に以下のメッセージが出てしまうため、様子見（JDK17では出力されない）
		// 「n.s.j.engine.design.JRJdk13Compiler : ノート:
		// クラス・パスに1つ以上のプロセッサが見つかったため、注釈処理が有効化されています。少なくとも1つのプロセッサが名前(-processor)で指定されるか、
		// 検索パス(--processor-path、--processor-module-path)が指定されるか、注釈処理が明示的に有効化(-proc:only、-proc:full)されている場合を除き、
		// 将来のリリースのjavacでは注釈処理が無効化される可能性があります。-Xlint:オプションを使用すると、このメッセージを非表示にできます。-proc:noneを使用すると、注釈処理を無効化できます。」

		File jrxmlFile = getJRXMLFile();
		File jasperFile = getJasperFile();
		appLogger.debug("帳票様式のコンパイル:{}", jrxmlFile.getAbsolutePath());
		// jrxmlの帳票様式ファイルをコンパイルする
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperCompileManager.html
		JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
		// コンパイル済の帳票様式を保存する
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/util/JRSaver.html
		JRSaver.saveObject(jasperReport, jasperFile);
		appLogger.debug("コンパイル結果ファイル:{}", jasperFile.getAbsolutePath());
		return jasperReport;
	}

	/**
	 * PDF形式で帳票を出力する
	 * 
	 * @param jasperPrint JasperPrintオブジェクト
	 * @param options     PDF出力時のオプション設定
	 * 
	 * @return PDFファイルのInputStreamデータ
	 * @throws JRException JasperReportsでPDFのエクスポートに失敗した場合
	 */
	private InputStream exportPDF(final JasperPrint jasperPrint, PDFOptions options) throws JRException {
		// 通常のPDF出力の実装例
		// byte[] reportContent = JasperExportManager.exportReportToPdf(jasperPrint);
		// return new ByteArrayInputStream(reportContent);

		// PDFのパスワード等を指定する場合には、直接JRPdfExporterインスタンスを作成しないとダメそう
		// https://javadoc.io/doc/net.sf.jasperreports/jasperreports-pdf/latest/net/sf/jasperreports/pdf/JRPdfExporter.html
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/pdf/PdfExporterConfiguration.html#getUserPassword()
		JRPdfExporter exporter = new JRPdfExporter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		if (options.isEncrypted()) {
			// PDFのパスワード等を指定する場合の設定
			SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
			configuration.setEncrypted(true);
			configuration.setUserPassword(options.getUserPassword());
			configuration.setOwnerPassword(options.getOwnerPassword());
			exporter.setConfiguration(configuration);
		}
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
		exporter.exportReport();
		return new ByteArrayInputStream(baos.toByteArray());
	}

	// 現状未使用だが、実装例として残しておき、必要な場合、こちらに切り替えることも検討
	/**
	 * PDF形式で帳票を出力する際、ファイルサイズが大きい場合の実装例
	 * 
	 * @return PDFファイルのInputStreamデータ
	 * @throws JRException JasperReportsでPDFのエクスポートに失敗した場合
	 * @throws IOException
	 */
	private InputStream exportPDFForLargeFile(final JasperPrint jasperPrint) throws JRException, IOException {
		// メモリを極力使わないよう、PDFのファイルサイズが大きい場合も考慮し一時ファイルに出力してInputStreamを返す実装例
		Path tempFilePath = Files.createTempFile("item-report", ".pdf");
		tempFilePath.toFile().deleteOnExit();
		// PDF形式で出力
		JasperExportManager.exportReportToPdfFile(jasperPrint, tempFilePath.toString());
		// 一時ファイルのInputStreamを返す
		return new BufferedInputStream(new FileInputStream(tempFilePath.toFile()));
	}

}
