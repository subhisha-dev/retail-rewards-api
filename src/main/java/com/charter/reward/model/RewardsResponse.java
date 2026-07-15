package com.charter.reward.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardsResponse {
    private String customerId;
    private String customerName;
    private int periodInMonths;
    private int totalTransactions;
    private List<TransactionDetail> transactions = new ArrayList<>();
    private List<MonthlyReward> monthlyRewardPoints = new ArrayList<>();
    private BigDecimal totalRewardPoints = BigDecimal.ZERO;
    private LocalDate startDate;
    private LocalDate endDate;
}
