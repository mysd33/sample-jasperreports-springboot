package com.example.fw.common.reports;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.fw.common.exception.SystemException;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
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

// TODO: SubReportの対応を検討

/**
 * Jasper Reportsを使用して、帳票作成のための基底クラス
 * 
 * @param <T> 帳票データの型
 */
@Slf4j
public abstract class AbstractJasperReportCreator<T> {
	private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
	private Path jasperPath;
	private ReportsConfigurationProperties config;

	@Autowired
	public void setConfig(ReportsConfigurationProperties config) {
		this.config = config;
	}

	@PostConstruct
	public void init() {
		// コンパイル済の帳票様式を保存する一時ディレクトリを作成する
		String tempDir = System.getProperty("java.io.tmpdir");
		jasperPath = Path.of(tempDir, config.getJasperFileTmpdir());
		// 一時ディレクトリが存在しない場合は作成する
		jasperPath.toFile().mkdirs();
		appLogger.debug("jasperPath: {}", jasperPath);
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
	 * @return 帳票のバイト配列
	 */
	public InputStream createPDFReport(final T data) {
		JasperReport jasperReport;
		try {
			File jasperFile = getJasperFile();
			// コンパイル済の帳票様式がある場合はそれを利用する
			jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
		} catch (FileNotFoundException | JRException e) {
			try {
				// コンパイル済の帳票様式がない場合はコンパイル処理するように実装する
				jasperReport = compileJRXML();

				// TODO: @PostConstructで、あらかじめコンパイル処理するように実装したところ、、複数帳票出力クラスがある場合に
				// 以下のメッセージが出てしまうため、サンプルAPと同様に、初回実行時になければコンパイル処理するように実装するように戻している。
				// 「n.s.j.engine.design.JRJdk13Compiler : ノート:
				// クラス・パスに1つ以上のプロセッサが見つかったため、注釈処理が有効化されています。少なくとも1つのプロセッサが名前(-processor)で指定されるか、
				// 検索パス(--processor-path、--processor-module-path)が指定されるか、注釈処理が明示的に有効化(-proc:only、-proc:full)されている場合を除き、
				// 将来のリリースのjavacでは注釈処理が無効化される可能性があります。-Xlint:オプションを使用すると、このメッセージを非表示にできます。-proc:noneを使用すると、注釈処理を無効化できます。」

			} catch (FileNotFoundException | JRException e1) {
				throw new SystemException(e1, CommonFrameworkMessageIds.I_CM_FW_0003);
			}
		}
		Map<String, Object> parameters = getParameters(data);
		JRDataSource dataSource = getDataSource(data);
		try {
			// 帳票様式に帳票データを渡して、帳票を作成する
			// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperFillManager.html
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			// PDF形式で出力
			// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperExportManager.html

			// そのままバイト配列に出力する実装例
			byte[] reportContent = JasperExportManager.exportReportToPdf(jasperPrint);
			return new ByteArrayInputStream(reportContent);

			// メモリを極力使わないよう、PDFのファイルサイズが大きい場合も考慮し一時ファイルに出力してInputStreamを返す実装例
			// Path tempFilePath = Files.createTempFile("item-report", ".pdf");
			// tempFilePath.toFile().deleteOnExit();
			// PDF形式で出力
			// JasperExportManager.exportReportToPdfFile(jasperPrint,
			// tempFilePath.toString());
			// 一時ファイルのInputStreamを返す
			// return new BufferedInputStream(new FileInputStream(tempFilePath.toFile()));
		} catch (JRException e) { // | IOException e) {
			throw new SystemException(e, CommonFrameworkMessageIds.I_CM_FW_0004);
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
	protected abstract Map<String, Object> getParameters(final T data);

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
		File jrxmlFile = getJRXMLFile();
		File jasperFile = getJasperFile();
		// jrxmlの帳票様式ファイルをコンパイルする
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperCompileManager.html
		JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
		// コンパイル済の帳票様式を保存する
		// https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/util/JRSaver.html
		JRSaver.saveObject(jasperReport, jasperFile);
		return jasperReport;
	}

}
