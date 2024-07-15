package com.example.jaspersample.domain.service.order;

import org.springframework.stereotype.Service;

import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

/**
 * OrderServiceの実装クラス
 */
@Service
@RequiredArgsConstructor
// @Transactional
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;

	@Override
	// @Transactional(readOnly = true)
	public Order findOne(String id) {
		return orderRepository.findOne(id);
	}
}
