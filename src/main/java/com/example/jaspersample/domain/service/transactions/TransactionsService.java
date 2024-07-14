package com.example.jaspersample.domain.service.transactions;

import java.io.InputStream;
import java.util.List;

import com.example.jaspersample.domain.model.Transaction;

/**
 * 取引サービス
 */
public interface TransactionsService {
	/**
	 * 取引の一覧を取得する
	 * @return 取引の一覧
	 */
	List<Transaction> findAll();

	/**
	 * 取引一覧の帳票を生成する
	 * 
	 * @return 取引一覧帳票の入力ストリーム
     */
	InputStream createTransactionsReport();
}
