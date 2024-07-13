package com.example.jasper.domain.service.items;

import java.io.InputStream;
import java.util.List;

import com.example.jasper.domain.model.Item;

/**
 * 商品サービス
 */
public interface ItemsService {

	/**
	 * 商品の一覧を取得する
	 * 
	 * @return 商品の一覧
	 */
	List<Item> findAll();

	/**
	 * 商品一覧帳票を生成する
	 */
	InputStream createItemsReport();

}
