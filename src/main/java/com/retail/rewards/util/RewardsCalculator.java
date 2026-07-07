package com.retail.rewards.util;

import com.retail.rewards.RewardsConstants;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class RewardsCalculator {

    private RewardsCalculator() {
    }

    public static double calculateRewardPoints(Double amount) {

        if (amount == null) {
            throw new IllegalArgumentException("Transaction amount can not be null");
        }
        if(amount <= 0) {
            throw new IllegalArgumentException("Transaction amount can not be negative:" + amount);
        }
        double points = 0;
        if(amount > RewardsConstants.PREMIUM_TIER_AMOUNT) {
            double premiumTierAmount =  amount - RewardsConstants.PREMIUM_TIER_AMOUNT ; //$120.87 - $100 = $20.87 * 2(rate) = $40.87
            points += premiumTierAmount * RewardsConstants.RATE_PER_PREMIUM_DOLLAR; // one point for each dollar between 50 and 100 = $50 * 1(rate) = $50
            log.info("Amount of each transaction which > $100: {} and the calculated points for that amount: {}", amount, points );
        }
        if(amount > RewardsConstants.STANDARD_TIER_AMOUNT) {
            double standardTierAmount = Math.min(amount, RewardsConstants.PREMIUM_TIER_AMOUNT) - 50.0;
            points += standardTierAmount * RewardsConstants.RATE_PER_STANDARD_DOLLAR;
            log.info("Amount of each transaction which is > $50 and <=100: {} and the calculated points for that amount: {}",
                    amount, points );
        }
    return BigDecimal.valueOf(points).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
