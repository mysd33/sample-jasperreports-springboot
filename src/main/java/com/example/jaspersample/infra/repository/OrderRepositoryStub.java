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
                .customer(Customer.builder().name("○○株式会社").zip("160-0023").address("東京都新宿区7-7-7").pdfPassword("1234")
                        .build())
                .billingSource(BillingSource.builder().name("××株式会社").zip("160-0023").address("東京都新宿区7-8-8")
                        .tel("03-XXXX-XXXX").manager("請求太郎").build())
                .orderItems(List.of(//
                        OrderItem.builder().itemCode("10101").itemName("パソコンデスク").quantity(10).unitPrice(59800)
                                .amount(598000).build(), //
                        OrderItem.builder().itemCode("10102").itemName("パソコンデスク").quantity(10).unitPrice(36800)
                                .amount(368000).build(), //
                        OrderItem.builder().itemCode("10110").itemName("パソコンデスク").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("10202").itemName("パソコンデスク").quantity(10).unitPrice(36800)
                                .amount(368000).build(), //
                        OrderItem.builder().itemCode("10110").itemName("パソコンデスク").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("10110").itemName("パソコンデスク").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("10110").itemName("パソコンデスク").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("10110").itemName("パソコンデスク").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("11301").itemName("事務机").quantity(10).unitPrice(59800)
                                .amount(598000).build(), //
                        OrderItem.builder().itemCode("11301").itemName("事務机").quantity(10).unitPrice(59800)
                                .amount(598000).build(), //
                        OrderItem.builder().itemCode("11301").itemName("事務机").quantity(10).unitPrice(59800)
                                .amount(598000).build(), //
//						OrderItem.builder().itemCode("11301").itemName("事務机").quantity(10).unitPrice(59800).amount(598000).build(),//
                        OrderItem.builder().itemCode("11301").itemName("事務机").quantity(10).unitPrice(59800)
                                .amount(598000).build(), //
                        OrderItem.builder().itemCode("11301").itemName("事務机").quantity(10).unitPrice(59800)
                                .amount(598000).build(), //
                        OrderItem.builder().itemCode("11301").itemName("事務机").quantity(10).unitPrice(59800)
                                .amount(598000).build(), //
                        OrderItem.builder().itemCode("13205").itemName("OAチェア").quantity(20).unitPrice(39800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("13205").itemName("OAチェア").quantity(20).unitPrice(39800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("13205").itemName("OAチェア").quantity(20).unitPrice(39800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("13205").itemName("OAチェア").quantity(20).unitPrice(39800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("13205").itemName("OAチェア").quantity(20).unitPrice(39800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("13205").itemName("OAチェア").quantity(20).unitPrice(39800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("13205").itemName("OAチェア").quantity(20).unitPrice(39800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("13205").itemName("OAチェア").quantity(20).unitPrice(39800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build(), //
                        OrderItem.builder().itemCode("20210").itemName("キャビネット").quantity(5).unitPrice(32800)
                                .amount(164000).build()))
                .build();
    }

}
