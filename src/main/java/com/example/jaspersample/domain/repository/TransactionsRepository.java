package com.example.jaspersample.domain.repository;

import java.util.List;

import com.example.jaspersample.domain.model.Transaction;

public interface TransactionsRepository {
	List<Transaction> findAll();
}
