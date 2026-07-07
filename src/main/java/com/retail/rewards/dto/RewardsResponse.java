package com.retail.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardsResponse {
    private String customerId;
    private Map<String, Double> monthlyRewardPoints; // key: yyyy-MM , value: reward points calculated on monthly basis
    private double totalRewardPoints;

}
