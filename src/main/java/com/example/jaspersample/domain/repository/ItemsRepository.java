package com.example.jaspersample.domain.repository;

import java.util.List;

import com.example.jaspersample.domain.model.Item;

/**
 * 商品情報のリポジトリインタフェース
 */
public interface ItemsRepository {

    /**
     * 商品一覧を取得する
     * 
     * @return 商品一覧
     */
    List<Item> findAll();
}
