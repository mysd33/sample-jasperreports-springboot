package com.example.jaspersample.domain.service.transactions;

import java.util.List;

import com.example.jaspersample.domain.model.Transaction;

/**
 * 取引サービスインタフェース
 */
public interface TransactionsService {
	/**
	 * 取引の一覧を取得する
	 * @return 取引の一覧
	 */
	List<Transaction> findAll();

}
