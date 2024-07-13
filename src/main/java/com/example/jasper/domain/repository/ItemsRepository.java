package com.example.jasper.domain.repository;

import java.util.List;

import com.example.jasper.domain.model.Item;

/**
 * 商品リポジトリ
 */ 
public interface ItemsRepository {

	/**
	 * 商品の一覧を取得する
	 * 
	 * @return 商品の一覧
	 */
	List<Item> findAll();
}
