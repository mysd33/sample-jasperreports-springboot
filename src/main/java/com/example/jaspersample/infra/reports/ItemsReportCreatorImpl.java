package com.example.jaspersample.infra.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ResourceUtils;

import com.example.fw.common.reports.AbstractJasperReportCreator;
import com.example.fw.common.reports.Report;
import com.example.fw.common.reports.ReportCreator;
import com.example.jaspersample.domain.model.Item;
import com.example.jaspersample.domain.reports.ItemsReportCreator;
import com.example.jaspersample.domain.reports.ReportFile;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * ItemReportCreatorの実装クラス<br>
 * 
 * 帳票出力のフレームワーク機能を利用して実装している
 */
@RequiredArgsConstructor
@ReportCreator(id = "R001", name = "商品一覧")
// AbstractJasperReportCreatorを継承
// 型パラメータに帳票作成に必要なデータの型を指定
public class ItemsReportCreatorImpl extends AbstractJasperReportCreator<List<Item>> implements ItemsReportCreator {
    private static final String TITLE = "title";
    private static final String REPORT_NAME = "商品一覧";
    private static final String REPORT_FILE_NAME = "商品一覧.pdf";
    private static final String JRXML_FILE_PATH = "classpath:reports/item-report.jrxml";

    @Override
    public ReportFile createItemsReport(List<Item> items) {
        // AbstractJasperReportCreatorが提供するcreatePDFReportメソッドをを呼び出すとPDF帳票作成する
        Report report = createPDFReport(items);

        return ReportFile.builder()//
                .inputStream(report.getInputStream())//
                .fileName(REPORT_FILE_NAME)//
                .fileSize(report.getSize())// )
                .build();

    }

    // AbstractJasperReportCreatorのabstractメソッドgetJRXMLFileを実装して様式ファイルのパスを返す
    @Override
    protected File getMainJRXMLFile() throws FileNotFoundException {
        return ResourceUtils.getFile(JRXML_FILE_PATH);
    }

    // AbstractJasperReportCreatorのメソッドgetParametersを実装して、帳票作成に必要なパラメータを返す
    @Override
    protected Map<String, Object> getParameters(List<Item> data) {
        Map<String, Object> parameters = new HashMap<>();
        // タイトルをパラメータに指定
        parameters.put(TITLE, REPORT_NAME);
        return parameters;
    }

    // AbstractJasperReportCreatorのabstractメソッドgetDataSourceを実装して、データソースを返す
    @Override
    protected JRDataSource getDataSource(List<Item> data) {
        return new JRBeanCollectionDataSource(data);
    }

}
