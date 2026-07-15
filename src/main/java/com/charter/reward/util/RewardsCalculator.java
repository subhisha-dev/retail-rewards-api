package com.charter.reward.util;

import com.charter.reward.constants.RewardsConstants;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class RewardsCalculator {

    private static final String NULL_AMOUNT_MESSAGE = "Transaction amount can not be null";
    private static final String NON_POSITIVE_AMOUNT_MESSAGE = "Transaction amount must be greater than zero: ";

    private RewardsCalculator() {
    }

    public static BigDecimal calculateRewardPoints(BigDecimal amount) {
        validateTransactionAmount(amount);

        BigDecimal points = BigDecimal.ZERO;

        if (amount.compareTo(RewardsConstants.PREMIUM_TIER_AMOUNT) > 0) {
            BigDecimal premiumRewardAmount = amount.subtract(RewardsConstants.PREMIUM_TIER_AMOUNT);
            points = points.add(premiumRewardAmount.multiply(RewardsConstants.RATE_PER_PREMIUM_DOLLAR));
            log.debug("Amount of each transaction which > $100: {} and the calculated points for that amount: {}", amount, points);
        }
        if (amount.compareTo(RewardsConstants.STANDARD_TIER_AMOUNT) > 0) {
            BigDecimal standardRewardUpperBound = amount.min(RewardsConstants.PREMIUM_TIER_AMOUNT);
            BigDecimal standardRewardAmount = standardRewardUpperBound.subtract(RewardsConstants.STANDARD_TIER_AMOUNT);
            points = points.add(standardRewardAmount.multiply(RewardsConstants.RATE_PER_STANDARD_DOLLAR));
            log.debug("Amount of each transaction which is > $50 and <=100: {} and the calculated points for that amount: {}", amount, points);
        }

        return points.setScale(2, RoundingMode.HALF_UP);
    }

    private static void validateTransactionAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException(NULL_AMOUNT_MESSAGE);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(NON_POSITIVE_AMOUNT_MESSAGE + amount);
        }
    }
}
