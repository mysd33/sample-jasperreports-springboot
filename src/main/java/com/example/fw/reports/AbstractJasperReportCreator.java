package com.example.fw.reports;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

/**
 * Jasper Reportsを使用して、帳票作成のための基底クラス
 * 
 * @param <T> 帳票データの型
 */
public abstract class AbstractJasperReportCreator<T> {
	// コンパイル済の帳票様式を保存する一時ディレクトリ名
	private static final String TEMP_JASPER_DIR = "jasper";
	private final Path jasperPath;

	public AbstractJasperReportCreator() {
		// コンパイル済の帳票様式を保存する一時ディレクトリを作成する
		String tempDir = System.getProperty("java.io.tmpdir");
		jasperPath = Path.of(tempDir, TEMP_JASPER_DIR);
		//System.out.println("jasperPath: " + jasperPath);
		// 一時ディレクトリが存在しない場合は作成する
		jasperPath.toFile().mkdirs();
	}

	@PostConstruct
	public void init() throws FileNotFoundException, JRException {
		// あらかじめjrxmlの帳票様式ファイルをコンパイルしておく
		File jrxmlFile = getJRXMLFile();
		File jasperFile = getJasperFile();
		JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
		// コンパイル済の帳票様式を保存する
		JRSaver.saveObject(jasperReport, jasperFile);
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
	public InputStream createPDFReport(T data) {

		JasperReport jasperReport;
		try {
			// コンパイル済の帳票様式がある場合はそれを利用する
			File jasperFile = getJasperFile();
			jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
		} catch (FileNotFoundException | JRException e) {
			// TODO: 実際にはSystemExceptionでスロー
			throw new RuntimeException("コンパイル済帳票テンプレートの読み込みに失敗しました", e);
		}
		Map<String, Object> parameters = getParameters(data);
		JRDataSource dataSource = getDataSource(data);
		try {
			// 帳票様式に帳票データを渡して、帳票を作成する
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			// そのままバイト配列に出力する実装例
			// PDF形式で出力
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
			// TODO: 実際にはSystemExceptionでスロー
			throw new RuntimeException("帳票の作成に失敗しました", e);
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
	protected abstract Map<String, Object> getParameters(T data);

	/**
	 * 帳票作成に必要なデータソースを取得する
	 * 
	 * @param data 帳票データ
	 * @return データソース
	 */
	protected abstract JRDataSource getDataSource(T data);

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

}
