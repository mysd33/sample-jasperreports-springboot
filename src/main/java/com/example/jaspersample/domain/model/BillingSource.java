package com.example.jaspersample.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 請求元情報のエンティティクラス
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingSource {
    // 請求元会社名
    private String name;
    // 郵便番号
    private String zip;
    // 住所
    private String address;
    // 電話番号
    private String tel;
    // 担当者名
    private String manager;
}
