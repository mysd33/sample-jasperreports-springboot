package com.example.jaspersample.infra.reports;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ResourceUtils;

import com.example.jaspersample.domain.model.Item;
import com.example.jaspersample.domain.reports.ItemsReportCreator;
import com.example.jaspersample.domain.reports.ReportFile;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

/**
 * JasperReportsのAPIをべた書きして使用したItemsReportCreator実装クラス<br>
 * 
 * JasperReportsのAPIの基本的な使い方を理解するために残しているが、
 * 通常は、フレームワーク機能を使ったItemsReportCreatorImplを利用する
 * 
 */
//@Component
public class ItemsReportCreatorImplByJasperAPISimple implements ItemsReportCreator {
    private static final String TITLE = "title";
    private static final String REPORT_NAME = "商品一覧";
    private static final String REPORT_FILE_NAME = "商品一覧.pdf";
    private static final String JRXML_FILE_PATH = "classpath:reports/item-report.jrxml";
    private static final String JASPER_FILE_PATH = "item-report.jasper";

    @Override
    public ReportFile createItemsReport(List<Item> items) {
        JasperReport jasperReport;

        try {
            // コンパイル済の帳票様式がある場合はそれを利用する
            jasperReport = (JasperReport) JRLoader.loadObject(ResourceUtils.getFile(JASPER_FILE_PATH));
        } catch (FileNotFoundException | JRException e) {
            try {
                // コンパイル済の帳票様式が見つからない場合は、jrxmlの帳票様式ファイルをコンパイルする
                File jrxmlFile = ResourceUtils.getFile(JRXML_FILE_PATH);
                jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
                // コンパイル済の帳票様式を保存する
                JRSaver.saveObject(jasperReport, JASPER_FILE_PATH);
            } catch (FileNotFoundException | JRException e1) {
                // TODO: 実際にはSystemExceptionでスロー
                throw new RuntimeException("帳票テンプレートの読み込みに失敗しました", e1);
            }
        }
        // 商品リストを、データソース（JRBeanCollectionDataSource）に指定
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);
        Map<String, Object> parameters = new HashMap<>();
        // タイトルをパラメータに指定
        parameters.put(TITLE, REPORT_NAME);

        try {
            // 帳票様式に帳票データを渡して、帳票を作成する
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // そのままバイト配列に出力する実装例
            // PDF形式で出力
            byte[] reportContent = JasperExportManager.exportReportToPdf(jasperPrint);
            InputStream is = new ByteArrayInputStream(reportContent);
            return ReportFile.builder()//
                    .inputStream(is)//
                    .fileSize(reportContent.length)//
                    .fileName(REPORT_FILE_NAME)//
                    .build();

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
}
