package com.example.jasper.domain.service.items;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jasper.domain.model.Item;
import com.example.jasper.domain.reports.ItemsReportCreator;
import com.example.jasper.domain.repository.ItemsRepository;

import lombok.RequiredArgsConstructor;

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
