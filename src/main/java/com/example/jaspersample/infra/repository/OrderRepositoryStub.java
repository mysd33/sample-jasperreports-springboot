package com.example.jaspersample.infra.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.jaspersample.domain.model.BillingSource;
import com.example.jaspersample.domain.model.Customer;
import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.model.OrderItem;
import com.example.jaspersample.domain.repository.OrderRepository;

/**
 * OrderRepositoryのスタブ
 */
@Repository
public class OrderRepositoryStub implements OrderRepository {

	@Override
	public Order findOne(String id) {
		return Order.builder()//
				.id(id)
				// TODO: 注文情報を設定
				.customer(Customer.builder().name("○○株式会社").zip("160-0023").address("東京都新宿区7-7-7").build())
				.billingSource(BillingSource.builder().name("××株式会社").zip("160-0023").address("東京都新宿区7-8-8").tel("03-XXXX-XXXX").manager("請求太郎").build())
				.orderItems(List.of(OrderItem.builder().itemCode("10101").build())).build();
	}

}
