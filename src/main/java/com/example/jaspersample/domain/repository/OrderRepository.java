package com.example.jaspersample.domain.repository;

import com.example.jaspersample.domain.model.Order;

/**
 * 注文情報のリポジトリインタフェース
 */
public interface OrderRepository {

    /**
     * 注文情報を取得する
     * 
     * @param id 注文ID
     * 
     * @return 注文情報
     */
    public Order findOne(String id);
}
