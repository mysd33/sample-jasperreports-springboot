package com.example.jasper.infra.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.jasper.domain.model.Item;
import com.example.jasper.domain.repository.ItemsRepository;

/**
 * ItemsRepositoryのスタブ
 */
@Repository
public class ItemRepositoryStub implements ItemsRepository {

	@Override
	public List<Item> findAll() {
		return List.of(//
				Item.builder().id(1001L).name("牛乳").build(), //
				Item.builder().id(1002L).name("ジュース").build(), //
				Item.builder().id(1003L).name("じゃがいも").build(), //
				Item.builder().id(1004L).name("牛肉").build(), //
				Item.builder().id(1005L).name("豚肉").build());
	}
}
