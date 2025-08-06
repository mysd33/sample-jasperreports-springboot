package com.example.jaspersample.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取引情報のエンティティクラス
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String date;
    private String time;
    private String site;
    private String fleet;
    private String vehicle;
    private String driver;
    private String authCode;
    private String taxPayerId;
    private String trnNumber;
    private String subAccount;
    private String identification;
    private String fuel;
    private double volume;
    private double amount;
    private double unitPrice;
}
