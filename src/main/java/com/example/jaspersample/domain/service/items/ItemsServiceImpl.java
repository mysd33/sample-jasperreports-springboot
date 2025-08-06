package com.example.jaspersample.domain.service.items;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jaspersample.domain.model.Item;
import com.example.jaspersample.domain.repository.ItemsRepository;

import lombok.RequiredArgsConstructor;

/**
 * ItemsServiceの実装クラス
 */
@Service
@RequiredArgsConstructor
// @Transactional
public class ItemsServiceImpl implements ItemsService {
	private final ItemsRepository itemRepository;

	@Override
	// @Transactional(readOnly = true)
	public List<Item> findAll() {
		return itemRepository.findAll();
	}
}
