package com.example.jaspersample.domain.reports;

import java.util.List;

import com.example.jaspersample.domain.model.Transaction;

/**
 * 取引に関する帳票作成のインタフェース
 */
public interface TransactionsReportCreator {

    /**
     * 取引一覧の帳票を作成する
     * 
     * @param items 帳票に出力する取引リスト
     * @return 取引一覧帳票の帳票ファイル
     */
    ReportFile createTransactionsReport(List<Transaction> items);
}
