package com.example.jaspersample.domain.reports;

import java.util.List;

import com.example.jaspersample.domain.model.Item;

/**
 * 商品に関する帳票作成のインタフェース
 */
public interface ItemsReportCreator {

    /**
     * 商品一覧の帳票を作成する
     * 
     * @param items 帳票に出力する商品リスト
     * 
     * @return 商品一覧の帳票ファイル
     */
    ReportFile createItemsReport(List<Item> items);
}
