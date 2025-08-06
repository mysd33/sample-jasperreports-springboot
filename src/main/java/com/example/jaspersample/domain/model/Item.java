package com.example.jaspersample.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品情報のエンティティクラス
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    // 商品ID
    private Long id;
    // 商品名
    private String name;
}
