package com.example.jaspersample.domain.reports;

/**
 * 請求に関する帳票作成のインタフェース（CSVファイル用）
 */
public interface InvoiceReportCreatorForCSV {
    /**
     * 請求書を作成する
     * 
     * @param csvData 帳票CSVファイルの入力ストリームデータ
     * @return 請求書の帳票ファイル
     */
    ReportFile createInvoice(InvoiceReportCSVData csvData);
}
