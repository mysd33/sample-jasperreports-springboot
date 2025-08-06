package com.example.jaspersample.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 顧客情報のエンティティクラス
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    // 顧客ID
    private String id;
    // 顧客名
    private String name;
    // 郵便番号
    private String zip;
    // 住所
    private String address;
    // PDFパスワード
    private String pdfPassword;
}
