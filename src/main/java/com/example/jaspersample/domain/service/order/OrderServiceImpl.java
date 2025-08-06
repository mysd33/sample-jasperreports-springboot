package com.example.jaspersample.domain.service.order;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.fw.common.exception.SystemException;
import com.example.jaspersample.domain.message.MessageIds;
import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.reports.InvoiceReportCSVData;
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

	// TODO: 実際は動的にCSVファイルを取得する。
	@Value("classpath:csv/order.csv")
	private Resource csvResource;

	@Override
	// @Transactional(readOnly = true)
	public Order findOne(String id) {
		return orderRepository.findOne(id);
	}

	@Override
	public InvoiceReportCSVData getReportCSVData(String id) {
		try {
			InputStream csvInputStream = csvResource.getInputStream();
			return InvoiceReportCSVData.builder()//
					.inputStream(csvInputStream)//
					.pdfPassword("1234")//
				.build();
		} catch (IOException e) {
			throw new SystemException(e, MessageIds.E_EX_9001);
		}
	}
}
