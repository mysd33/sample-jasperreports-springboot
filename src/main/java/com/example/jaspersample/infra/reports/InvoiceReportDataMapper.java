package com.example.jaspersample.infra.reports;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.model.OrderItem;
import org.mapstruct.Mapping;

@Mapper(componentModel = ComponentModel.SPRING)
public interface InvoiceReportDataMapper {

    /**
     * 請求書の帳票出力用データの鏡部分、明細部分を一緒にマッピングする
     * 
     * @param order     注文
     * @param orderItem 注文明細
     * @return 請求書の帳票出力用データ
     */
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "customerName", source = "order.customer.name")
    @Mapping(target = "customerZip", source = "order.customer.zip")
    @Mapping(target = "customerAddress", source = "order.customer.address")
    @Mapping(target = "billingSourceName", source = "order.billingSource.name")
    @Mapping(target = "billingSourceZip", source = "order.billingSource.zip")
    @Mapping(target = "billingSourceAddress", source = "order.billingSource.address")
    @Mapping(target = "billingSourceTel", source = "order.billingSource.tel")
    @Mapping(target = "billingSourceManager", source = "order.billingSource.manager")
    InvoiceReportData modelToReportData(Order order, OrderItem orderItem);

    /**
     * 請求書の帳票出力用データの明細部分のみをマッピングする
     * 
     * @param orderItem 注文明細
     * @return 請求書の帳票出力用データ
     */
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "customerZip", ignore = true)
    @Mapping(target = "customerAddress", ignore = true)
    @Mapping(target = "billingSourceName", ignore = true)
    @Mapping(target = "billingSourceZip", ignore = true)
    @Mapping(target = "billingSourceAddress", ignore = true)
    @Mapping(target = "billingSourceTel", ignore = true)
    @Mapping(target = "billingSourceManager", ignore = true)
    InvoiceReportData modelToReportDataItemOnly(OrderItem orderItem);
}
