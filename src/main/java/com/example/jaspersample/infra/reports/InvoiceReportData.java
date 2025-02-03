package com.example.jaspersample.infra.reports;

import lombok.Data;

/**
 * 請求書の帳票出力用データクラス
 */
@Data
public class InvoiceReportData {
    // プロパティ名は帳票テンプレートのフィールド名と一致させる
    // 単項目
    // 注文ID
    private String orderId;
    // 顧客名
    private String customerName;
    // 顧客郵便番号
    private String customerZip;
    // 顧客住所
    private String customerAddress;
    // 請求元会社名
    private String billingSourceName;
    // 請求元郵便番号
    private String billingSourceZip;
    // 請求元住所
    private String billingSourceAddress;
    // 請求元電話番号
    private String billingSourceTel;
    // 請求元担当者名
    private String billingSourceManager;

    // 明細（繰り返し）項目
    // 商品コード
    private String itemCode;
    // 商品名
    private String itemName;
    // 数量
    private int quantity;
    // 単価
    private int unitPrice;
    // 金額
    private int amount;
    // 備考
    private String note;

}
