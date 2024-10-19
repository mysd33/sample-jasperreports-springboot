package com.example.fw.common.reports;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.fw.common.exception.SystemException;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;
import com.example.fw.common.message.CommonFrameworkMessageIds;
import com.example.fw.common.reports.config.ReportsConfigurationProperties;
import com.example.fw.common.systemdate.SystemDateUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.PdfExporterConfiguration;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;
import net.sf.jasperreports.pdf.type.PdfPermissionsEnum;

/**
 * JasperReportsを使用して、帳票作成のための基底クラス
 * 
 * @param <T> 帳票データの型
 */
@Slf4j
public abstract class AbstractJasperReportCreator<T> {
	private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
	private static final MonitoringLogger monitoringLogger = LoggerFactory.getMonitoringLogger(log);
	private ReportsConfigurationProperties config;
	private Path jasperPath;
	private String reportId;
	private String reportName;

	@Autowired
	public void setConfig(final ReportsConfigurationProperties config) {
		this.config = config;
	}

	/**
	 * 初期化処理
	 * 
	 * コンパイル済の帳票様式を保存する一時ディレクトリを作成し、帳票様式をコンパイルする
	 * 
	 * @throws FileNotFoundException jrxmlの様式ファイルの様式ファイルが存在しない場合
	 * @throws JRException           様式のコンパイルエラーまたはコンパイル済様式の保存時のエラー
	 */
	@PostConstruct
	public void init() throws FileNotFoundException, JRException {
		// ログ出力用に、帳票ID、帳票名を取得
		ReportCreator annotation = getClass().getAnnotation(ReportCreator.class);
		reportId = annotation.id();
		reportName = annotation.name();

		// コンパイル済の帳票様式を保存する一時ディレクトリを作成する
		String tempDir = System.getProperty("java.io.tmpdir");
		jasperPath = Path.of(tempDir, config.getJasperFileTmpdir());
		appLogger.debug("jasperPath: {}", jasperPath);
		// 一時ディレクトリが存在しない場合は作成する
		jasperPath.toFile().mkdirs();
		// あらかじめ帳票様式ファイルをコンパイルする
		try {
			// メインの帳票様式
			compileMainJRXML();
			// サブレポート用の帳票様式
			compileSubReportJRXML();
		} catch (FileNotFoundException | JRException e) {
			monitoringLogger.error(CommonFrameworkMessageIds.E_CM_FW_9003, e, reportId, reportName);
			throw e;
		}

	}

	@PreDestroy
	public void destroy() throws IOException {
		// メインの帳票様式のjapserファイルを削除する
		deleteJasperFile(getMainJasperFile());
		// サブレポート用の帳票様式のjapserファイルを削除する
		for (File subReportJaperFile : getSubReportJapserFiles()) {
			deleteJasperFile(subReportJaperFile);
		}
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
		appLogger.info(CommonFrameworkMessageIds.I_CM_FW_0007, reportId, reportName);
		// 処理時間を計測しログ出力
		long startTime = System.nanoTime();
		try {
			// 本処理
			return doCreatePDFReport(data, options);
		} finally {
			// 呼び出し処理実行後、処理時間を計測しログ出力
			long endTime = System.nanoTime();
			double elapsedTime = SystemDateUtils.calcElaspedTimeByMilliSecounds(startTime, endTime);
			appLogger.info(CommonFrameworkMessageIds.I_CM_FW_0008, reportId, reportName, elapsedTime);
		}
	}

	/**
	 * 帳票作成の本処理
	 * 
	 * @param data    帳票データ
	 * @param options PDF出力時のオプション設定
	 * @return PDFファイルのInputStreamデータ
	 */
	private InputStream doCreatePDFReport(final T data, final PDFOptions options) {
		Map<String, Object> parameters = getParameters(data);
		JRDataSource dataSource = getDataSource(data);
		JasperReport jasperReport = null;
		try {
			File jasperFile = getMainJasperFile();
			// コンパイル済の帳票様式（jasperファイル）を読み込む
			jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
		} catch (FileNotFoundException | JRException e) {
			throw new SystemException(e, CommonFrameworkMessageIds.E_CM_FW_9004, reportId, reportName);
		}

		try {
			// 帳票様式に帳票データを渡して、帳票を作成する
			// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperFillManager.html
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			// そのままバイト配列に出力する実装例
			return exportPDF(jasperPrint, options);

			// 一時ファイルに出力する実装例
			// return exportPDFForLargeFile(jasperPrint, options);
		} catch (JRException e) { // | IOException e) {
			throw new SystemException(e, CommonFrameworkMessageIds.E_CM_FW_9005, reportId, reportName);
		}
	}

	/**
	 * メインの帳票様式ファイル(jrxmlファイル)を取得する
	 * 
	 * @return 帳票様式ファイル
	 * @throws FileNotFoundException 様式ファイルが見つからなかった場合
	 */
	protected abstract File getMainJRXMLFile() throws FileNotFoundException;

	/**
	 * サブレポート用のの帳票様式ファイル(jrxmlファイル)を取得する サブレポートがある場合のみオーバライドする。
	 * 
	 * @return 帳票様式ファイル
	 * @throws FileNotFoundException 様式ファイルが見つからなかった場合
	 */
	protected List<File> getSubReportJRXMLFiles() throws FileNotFoundException {
		// デフォルトでは、サブレポートがない場合を想定して、空のリストを返却
		return new ArrayList<>();
	}

	/**
	 * 帳票作成に必要なパラメータを取得する パラメータを定義する場合のみオーバライドする。
	 * 
	 * @param data 帳票データ
	 * @return パラメータ
	 */
	protected Map<String, Object> getParameters(final T data) {
		return new HashMap<>();
	}

	/**
	 * 帳票作成に必要なデータソースを取得する
	 * 
	 * @param data 帳票データ
	 * @return データソース
	 */
	protected abstract JRDataSource getDataSource(final T data);

	/**
	 * コンパイル済のメインの帳票様式ファイル(jasperファイル)を取得する
	 * 
	 * @param jrxmlFile
	 * @return コンパイル済の帳票様式ファイル
	 * @throws FileNotFoundException jrxmlの様式ファイルが見つからない場合
	 */
	private File getMainJasperFile() throws FileNotFoundException {
		return getJasperFile(getMainJRXMLFile());
	}

	/**
	 * コンパイル済のサブレポート用の帳票様式ファイル(jasperファイル)を取得する
	 * 
	 * @return コンパイル済の帳票様式ファイル
	 * @throws FileNotFoundException jrxmlの様式ファイルが見つからない場合
	 */
	private List<File> getSubReportJapserFiles() throws FileNotFoundException {
		return getSubReportJRXMLFiles().stream().map(this::getJasperFile).toList();
	}

	/**
	 * コンパイル済の帳票様式ファイル(jasperファイル)を取得する
	 * 
	 * @param jrxmlFile
	 * @return コンパイル済の帳票様式ファイル
	 */
	private File getJasperFile(final File jrxmlFile) {
		String jasperFileName = jrxmlFile.getName().replace(".jrxml", ".jasper");
		// 一時フォルダにあるファイルパスを返却
		return jasperPath.resolve(jasperFileName).toFile();
	}

	/**
	 * メインのjrxmlの帳票様式ファイルをコンパイルする
	 * 
	 * @return コンパイル済の帳票様式（JasperReport）
	 * @throws FileNotFoundException jrxmlの様式ファイルが見つからない場合
	 * @throws JRException           様式のコンパイルエラーまたはコンパイル済の帳票の保存時のエラー
	 */
	private JasperReport compileMainJRXML() throws FileNotFoundException, JRException {
		File jrxmlFile = getMainJRXMLFile();
		return compileJRXML(jrxmlFile);
	}

	/**
	 * サブレポート用のjrxmlの帳票様式ファイルをコンパイルする
	 * 
	 * @return コンパイル済の帳票様式（JasperReport）
	 * @throws FileNotFoundException jrxmlの様式ファイルが見つからない場合
	 * @throws JRException           様式のコンパイルエラーまたはコンパイル済の帳票の保存時のエラー
	 */
	private List<JasperReport> compileSubReportJRXML() throws FileNotFoundException, JRException {
		List<File> jrxmlFiles = getSubReportJRXMLFiles();
		ArrayList<JasperReport> jasperReports = new ArrayList<>();
		for (File jrxmlFile : jrxmlFiles) {
			jasperReports.add(compileJRXML(jrxmlFile));
		}
		return jasperReports;
	}

	/**
	 * jrxmlの帳票様式ファイルをコンパイルする
	 * 
	 * @param jrxmlFile jrxmlの様式ファイル
	 * 
	 * @return コンパイル済の帳票様式（JasperReport）
	 * @throws JRException 様式のコンパイルエラーまたはコンパイル済の帳票の保存時のエラー
	 */
	private JasperReport compileJRXML(final File jrxmlFile) throws JRException {
		// TODO: JDK21の場合は、帳票コンパイル時に以下のメッセージが出てしまうため、様子見（JDK17では出力されない）
		// 「n.s.j.engine.design.JRJdk13Compiler : ノート:
		// クラス・パスに1つ以上のプロセッサが見つかったため、注釈処理が有効化されています。少なくとも1つのプロセッサが名前(-processor)で指定されるか、
		// 検索パス(--processor-path、--processor-module-path)が指定されるか、注釈処理が明示的に有効化(-proc:only、-proc:full)されている場合を除き、
		// 将来のリリースのjavacでは注釈処理が無効化される可能性があります。-Xlint:オプションを使用すると、このメッセージを非表示にできます。-proc:noneを使用すると、注釈処理を無効化できます。」

		appLogger.info(CommonFrameworkMessageIds.I_CM_FW_0005, reportId, reportName, jrxmlFile.getAbsolutePath());
		// jrxmlの帳票様式ファイルをコンパイルする
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperCompileManager.html
		JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
		// コンパイル後のjapserファイルを保存する
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/util/JRSaver.html
		File jasperFile = getJasperFile(jrxmlFile);
		JRSaver.saveObject(jasperReport, jasperFile);
		appLogger.info(CommonFrameworkMessageIds.I_CM_FW_0006, reportId, reportName, jasperFile.getAbsolutePath());
		return jasperReport;
	}

	/**
	 * japserファイルを削除する
	 * 
	 * @param jasperFile japserファイル
	 * @throws IOException
	 */
	private void deleteJasperFile(final File jasperFile) throws IOException {
		if (!jasperFile.exists()) {
			appLogger.info(CommonFrameworkMessageIds.I_CM_FW_0009, reportId, reportName, jasperFile.getAbsolutePath());
			return;
		}
		try {
			Files.delete(jasperFile.toPath());
		} catch (IOException e) {
			appLogger.warn(CommonFrameworkMessageIds.W_CM_FW_8001, reportId, reportName, jasperFile.getAbsolutePath());
			throw e;
		}
		appLogger.info(CommonFrameworkMessageIds.I_CM_FW_0010, reportId, reportName, jasperFile.getAbsolutePath());
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
	private InputStream exportPDF(final JasperPrint jasperPrint, final PDFOptions options) throws JRException {
		// （参考）通常のPDF出力の実装例
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperExportManager.html
		//
		// byte[] reportContent = JasperExportManager.exportReportToPdf(jasperPrint);
		// return new ByteArrayInputStream(reportContent);

		JRPdfExporter exporter = new JRPdfExporter();
		// PDFのセキュリティ設定を取得
		SimplePdfExporterConfiguration configuration = getPdfExproterConfiguration(options);
		if (configuration != null) {
			exporter.setConfiguration(configuration);
		}
		// PDFの出力
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
		exporter.exportReport();
		return new ByteArrayInputStream(baos.toByteArray());
	}

	// TODO: 現状未使用だが、実装例として残しておき、必要な場合、こちらに切り替えることも検討
	/**
	 * PDF形式で帳票を出力する際、ファイルサイズが大きい場合の実装例
	 * 
	 * @param jasperPrint JasperPrintオブジェクト
	 * @param options     PDF出力時のオプション設定
	 * 
	 * @return PDFファイルのInputStreamデータ
	 * @throws JRException JasperReportsでPDFのエクスポートに失敗した場合
	 * @throws IOException
	 */
	private InputStream exportPDFForLargeFile(final JasperPrint jasperPrint, final PDFOptions options)
			throws JRException, IOException {
		// メモリを極力使わないよう、PDFのファイルサイズが大きい場合も考慮し一時ファイルに出力してInputStreamを返す実装例
		Path tempFilePath = Files.createTempFile("item-report", ".pdf");
		tempFilePath.toFile().deleteOnExit();

		// （参考）PDF形式で出力
		// JasperExportManager.exportReportToPdfFile(jasperPrint,
		// tempFilePath.toString());

		JRPdfExporter exporter = new JRPdfExporter();
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFilePath.toFile()))) {
			SimplePdfExporterConfiguration configuration = getPdfExproterConfiguration(options);
			if (configuration != null) {
				exporter.setConfiguration(configuration);
			}
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
			exporter.exportReport();
		}
		// 一時ファイルのInputStreamを返す
		return new BufferedInputStream(new FileInputStream(tempFilePath.toFile()));
	}

	/**
	 * PDF出力時のオプション設定を取得する
	 * 
	 * @param options PDFオプション
	 * @return JasperReportsの設定（SimplePdfExporterConfiguration）
	 */
	private SimplePdfExporterConfiguration getPdfExproterConfiguration(final PDFOptions options) {
		if (!options.isEncrypted()) {
			return null;
		}
		// PDFのパスワード等を指定する場合には、直接JRPdfExporterインスタンスを作成し実装
		// https://javadoc.io/doc/net.sf.jasperreports/jasperreports-pdf/latest/net/sf/jasperreports/pdf/JRPdfExporter.html
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/pdf/PdfExporterConfiguration.html#getUserPassword()

		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		// パスワード指定時は、暗号化設定を有効化
		configuration.setEncrypted(true);
		// 閲覧パスワード
		configuration.setUserPassword(options.getUserPassword());
		// 権限パスワード
		configuration.setOwnerPassword(options.getOwnerPassword());

		// 暗号化設定
		if (options.has128bitKey()) {
			// PDFOptionsで設定されている場合は、優先的に使用
			configuration.set128BitKey(options.getIs128bitKey());
		} else {
			configuration.set128BitKey(config.is128bitKey());
		}
		// 権限の設定
		if (options.hasPermissionsDenied()) {
			// PDFOptionsで設定されている場合は、優先的に使用
			int permissionsDenied = 0;
			for (PdfPermissionsEnum permissionDenied : options.getPermissionsDenied()) {
				if (PdfPermissionsEnum.ALL.equals(permissionDenied)) {
					permissionsDenied = PdfExporterConfiguration.ALL_PERMISSIONS.intValue();
					break;
				}
				permissionsDenied |= permissionDenied.getPdfPermission();
			}
			configuration.setPermissions(~permissionsDenied);
		} else {
			String permissionsDenied = config.getPdfPermissionDenied();
			if (permissionsDenied != null && permissionsDenied.length() > 0) {
				// PDFOptionsの設定がない場合は、application.ymlの設定を使用
				configuration.setPermissions(~JRPdfExporter.getIntegerPermissions(permissionsDenied));
			} else {
				// application.ymlにも設定がない場合は、全ての権限を許可
				configuration.setPermissions(PdfExporterConfiguration.ALL_PERMISSIONS);
			}
		}
		return configuration;
	}
}