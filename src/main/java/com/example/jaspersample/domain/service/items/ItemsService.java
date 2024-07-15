package com.example.jaspersample.domain.service.items;

import java.util.List;

import com.example.jaspersample.domain.model.Item;

/**
 * 商品サービスインタフェース
 */
public interface ItemsService {

	/**
	 * 商品の一覧を取得する
	 * 
	 * @return 商品の一覧
	 */
	List<Item> findAll();
}
