package com.example.jaspersample.domain.service.order;

import com.example.jaspersample.domain.model.Order;

/**
 * 注文サービスインタフェース
 */
public interface OrderService {
	/**
	 * 注文情報を取得する
	 * 
	 * @param id 注文ID
	 * @return 注文情報
	 */
	Order findOne(String id);

}
