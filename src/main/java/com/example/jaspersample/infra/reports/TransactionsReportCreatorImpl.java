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
import com.example.jaspersample.domain.model.Transaction;
import com.example.jaspersample.domain.reports.ReportFile;
import com.example.jaspersample.domain.reports.TransactionsReportCreator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * TransactionReportCreatorの実装クラス<br>
 * 
 * 帳票出力のフレームワーク機能を利用して実装している
 */
@ReportCreator(id = "R002", name = "取引一覧")
public class TransactionsReportCreatorImpl extends AbstractJasperReportCreator<List<Transaction>>
        implements TransactionsReportCreator {
    private static final String TITLE = "title";
    private static final String REPORT_NAME = "取引一覧";
    private static final String REPORT_FILE_NAME = "取引一覧.pdf";    
    private static final String JRXML_FILE_PATH = "classpath:reports/transaction-report.jrxml";

    @Override
    public ReportFile createTransactionsReport(List<Transaction> items) {
        Report report = createPDFReport(items);
        return ReportFile.builder()//
                .inputStream(report.getInputStream())//
                .fileName(REPORT_FILE_NAME)//
                .fileSize(report.getSize())//
                .build();
    }

    @Override
    protected File getMainJRXMLFile() throws FileNotFoundException {
        return ResourceUtils.getFile(JRXML_FILE_PATH);
    }

    @Override
    protected Map<String, Object> getParameters(List<Transaction> data) {
        Map<String, Object> parameters = new HashMap<>();
        // タイトルをパラメータに指定
        parameters.put(TITLE, REPORT_NAME);
        return parameters;
    }

    @Override
    protected JRDataSource getDataSource(List<Transaction> data) {
        return new JRBeanCollectionDataSource(data);
    }

}
