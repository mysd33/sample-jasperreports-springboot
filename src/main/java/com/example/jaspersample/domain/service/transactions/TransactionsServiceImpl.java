package com.example.jaspersample.domain.service.transactions;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jaspersample.domain.model.Transaction;
import com.example.jaspersample.domain.repository.TransactionsRepository;

import lombok.RequiredArgsConstructor;

/**
 * TransactionsServiceの実装クラス
 */
@Service
@RequiredArgsConstructor
//@Transactional
public class TransactionsServiceImpl implements TransactionsService {
	private final TransactionsRepository transactionRepository;

	@Override
	// @Transactional(readOnly = true)
	public List<Transaction> findAll() {
		return transactionRepository.findAll();
	}

}
