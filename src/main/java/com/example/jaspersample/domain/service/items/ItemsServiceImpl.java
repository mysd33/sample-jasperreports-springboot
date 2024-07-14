package com.example.jaspersample.domain.service.items;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jaspersample.domain.model.Item;
import com.example.jaspersample.domain.reports.ItemsReportCreator;
import com.example.jaspersample.domain.repository.ItemsRepository;

import lombok.RequiredArgsConstructor;

/**
 * ItemsServiceの実装クラス
 */
@Service
@RequiredArgsConstructor
public class ItemsServiceImpl implements ItemsService {
	private final ItemsRepository itemRepository;
	private final ItemsReportCreator reportCreator;

	@Override
	public List<Item> findAll() {
		return itemRepository.findAll();
	}

	@Override
	public InputStream createItemsReport() {
		return reportCreator.createItemListReport(findAll());
	}

}
