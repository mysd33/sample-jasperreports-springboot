package com.example.jaspersample.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注文のエンティティクラス
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    // 注文ID
    private String id;
    // 顧客
    private Customer customer;
    // 請求元
    private BillingSource billingSource;

    // 注文明細
    private List<OrderItem> orderItems;

}
