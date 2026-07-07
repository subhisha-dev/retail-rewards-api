package com.retail.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Map<String, Double> monthlyRewardPoints = new HashMap<>(); // key: yyyy-MM , value: reward points calculated on monthly basis
    private double totalRewardPoints;

    public RewardsResponse(String customerId, Map<String, Double> monthlyPoints, double totalPoints) {
        this.customerId = customerId;
        this.monthlyRewardPoints = monthlyPoints == null ? new HashMap<>() : monthlyPoints;
        this.totalRewardPoints = totalPoints;
    }
}
