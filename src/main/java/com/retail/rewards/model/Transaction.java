package com.retail.rewards.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private String transactionId;
    private String customerId;
    private Double transAmount;
    private String customerName;

    @JsonFormat(pattern = "")
    private LocalDate transDate;
}
