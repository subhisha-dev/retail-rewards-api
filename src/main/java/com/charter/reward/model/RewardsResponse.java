package com.charter.reward.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardsResponse {
    private String customerId;
    private String customerName;
    private int periodInMonths;
    private int totalTransactions;
    private Map<String, BigDecimal> monthlyRewardPoints = new HashMap<>();
    private BigDecimal totalRewardPoints = BigDecimal.ZERO;
    private LocalDate startDate;
    private LocalDate endDate;
}
