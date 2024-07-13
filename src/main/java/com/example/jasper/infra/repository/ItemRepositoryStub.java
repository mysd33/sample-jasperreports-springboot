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
				Item.builder().id(1001L).name("milk").build(), //
				Item.builder().id(1002L).name("juice").build(), //
				Item.builder().id(1003L).name("poteto").build(), //
				Item.builder().id(1004L).name("beaf").build(), //
				Item.builder().id(1005L).name("poak").build());
	}
}
