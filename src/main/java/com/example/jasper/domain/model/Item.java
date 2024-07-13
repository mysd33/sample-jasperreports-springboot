package com.example.jasper.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
	// 商品ID
	private Long id;
	// 商品名
	private String name;
}
