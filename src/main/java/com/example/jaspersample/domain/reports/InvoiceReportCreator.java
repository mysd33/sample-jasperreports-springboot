package com.example.jaspersample.domain.reports;

import com.example.jaspersample.domain.model.Order;

/**
 * 請求に関する帳票作成のインタフェース
 */
public interface InvoiceReportCreator {
    /**
     * 請求書を作成する
     * 
     * @param order 請求対象の注文情報
     * @return 請求書の帳票ファイル
     */
    ReportFile createInvoice(Order order);
}
