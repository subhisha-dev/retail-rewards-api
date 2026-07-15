package com.charter.reward.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyReward {
    private int year;
    private String month;
    private BigDecimal rewardPoints;
}
