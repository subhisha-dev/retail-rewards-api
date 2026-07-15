package com.charter.reward.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetail {

    private String transactionId;
    private LocalDate transactionDate;
    private BigDecimal transactionAmount;
    private BigDecimal rewardPoints;
    private int year;
    private String month;
}
