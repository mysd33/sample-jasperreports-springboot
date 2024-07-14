package com.example.jaspersample.domain.service.transactions;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jaspersample.domain.model.Transaction;
import com.example.jaspersample.domain.reports.TransactionsReportCreator;
import com.example.jaspersample.domain.repository.TransactionsRepository;

import lombok.RequiredArgsConstructor;

/**
 * TransactionsServiceの実装クラス
 */
@Service
@RequiredArgsConstructor
public class TransactionsServiceImpl implements TransactionsService {
	private final TransactionsRepository transactionRepository;
	private final TransactionsReportCreator reportCreator;
	
	public List<Transaction> findAll() {
		return transactionRepository.findAll();
	}
	
	@Override
	public InputStream createTransactionsReport() {		
		return reportCreator.createTransactionListReport(findAll());
	}
}
