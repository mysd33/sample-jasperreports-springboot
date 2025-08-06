package com.example.jaspersample.domain.repository;

import java.util.List;

import com.example.jaspersample.domain.model.Transaction;

/**
 * 取引情報のリポジトリインタフェース
 */
public interface TransactionsRepository {
    /**
     * 取引情報の一覧を取得する
     * 
     * @return 取引情報の一覧
     */
    List<Transaction> findAll();
}
