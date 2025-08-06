package com.example.jaspersample.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注文明細のエンティティクラス
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
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
    @Builder.Default
    private String note = "";

}
