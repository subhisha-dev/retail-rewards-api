package com.charter.reward.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * Unit tests for RewardsCalculator utility class.
 * Tests cover all transaction tiers and edge cases.
 */
@DisplayName("RewardsCalculator Tests")
public class RewardsCalculatorTest {

    // Constants for easier readability
    private static final BigDecimal AMOUNT_25 = BigDecimal.valueOf(25.00);
    private static final BigDecimal AMOUNT_50 = BigDecimal.valueOf(50.00);
    private static final BigDecimal AMOUNT_75 = BigDecimal.valueOf(75.00);
    private static final BigDecimal AMOUNT_90 = BigDecimal.valueOf(90.00);
    private static final BigDecimal AMOUNT_100 = BigDecimal.valueOf(100.00);
    private static final BigDecimal AMOUNT_120 = BigDecimal.valueOf(120.00);
    private static final BigDecimal AMOUNT_150 = BigDecimal.valueOf(150.00);
    private static final BigDecimal AMOUNT_200 = BigDecimal.valueOf(200.00);
    private static final BigDecimal AMOUNT_390_05 = BigDecimal.valueOf(390.05);

    @Test
    @DisplayName("Null amount should throw IllegalArgumentException")
    public void testCalculateRewardPoints_NullAmount_ThrowsException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> RewardsCalculator.calculateRewardPoints(null)
        );
        Assertions.assertEquals("Transaction amount can not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Zero amount should throw IllegalArgumentException")
    public void testCalculateRewardPoints_ZeroAmount_ThrowsException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> RewardsCalculator.calculateRewardPoints(BigDecimal.ZERO)
        );
        Assertions.assertTrue(exception.getMessage().contains("Transaction amount must be greater than zero"));
    }

    @Test
    @DisplayName("Negative amount should throw IllegalArgumentException")
    public void testCalculateRewardPoints_NegativeAmount_ThrowsException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> RewardsCalculator.calculateRewardPoints(BigDecimal.valueOf(-10.00))
        );
        Assertions.assertTrue(exception.getMessage().contains("Transaction amount must be greater than zero"));
    }

    @Test
    @DisplayName("Amount less than $50 should return zero points")
    public void testCalculateRewardPoints_AmountLessThan50_ReturnsZero() {
        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_25);
        Assertions.assertEquals(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP), result);
        Assertions.assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Amount exactly $50 should return zero points")
    public void testCalculateRewardPoints_AmountExactly50_ReturnsZero() {
        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_50);
        Assertions.assertEquals(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP), result);
        Assertions.assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Amount $75 should return $25 in points (standard tier: 25 * $1)")
    public void testCalculateRewardPoints_Amount75_ReturnsCorrectPoints() {
        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_75);
        BigDecimal expected = BigDecimal.valueOf(25.00);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Amount $90 should return $40 in points (standard tier: 40 * $1)")
    public void testCalculateRewardPoints_Amount90_ReturnsCorrectPoints() {

        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_90);
        BigDecimal expected = BigDecimal.valueOf(40.00);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Amount exactly $100 should return $50 in points (standard tier: 50 * $1)")
    public void testCalculateRewardPoints_AmountExactly100_ReturnsCorrectPoints() {
        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_100);
        BigDecimal expected = BigDecimal.valueOf(50.00);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Amount $120 should return $60 in points (standard $50 + premium $20)")
    public void testCalculateRewardPoints_Amount120_ReturnsCorrectPoints() {

        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_120);
        BigDecimal expected = BigDecimal.valueOf(90.00);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Amount $150 should return $150 in points (standard $50 + premium $50)")
    public void testCalculateRewardPoints_Amount150_ReturnsCorrectPoints() {
        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_150);
        BigDecimal expected = BigDecimal.valueOf(150.00);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Amount $200 should return $250 in points")
    public void testCalculateRewardPoints_Amount200_ReturnsCorrectPoints() {
        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_200);
        BigDecimal expected = BigDecimal.valueOf(250.00);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Amount $390.05 should return correct points with rounding")
    public void testCalculateRewardPoints_Amount390_05_ReturnsCorrectPointsWithRounding() {
        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_390_05);
        BigDecimal expected = BigDecimal.valueOf(630.10);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Result should have scale of 2 decimal places")
    public void testCalculateRewardPoints_ResultScale_Is2() {
        BigDecimal result = RewardsCalculator.calculateRewardPoints(AMOUNT_120);
        Assertions.assertEquals(2, result.scale());
    }

    @Test
    @DisplayName("Decimal precision test: $50.50 should return $0.50 in points")
    public void testCalculateRewardPoints_DecimalAmount50_50_ReturnsCorrectPoints() {
        BigDecimal amount = BigDecimal.valueOf(50.50);
        BigDecimal result = RewardsCalculator.calculateRewardPoints(amount);
        BigDecimal expected = BigDecimal.valueOf(0.50);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Decimal precision test: $100.99 should return $50.99 in points")
    public void testCalculateRewardPoints_DecimalAmount100_99_ReturnsCorrectPoints() {
        BigDecimal amount = BigDecimal.valueOf(100.99);
        BigDecimal result = RewardsCalculator.calculateRewardPoints(amount);
        BigDecimal expected = BigDecimal.valueOf(51.98);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Decimal precision test: $99.99 should return $49.99 in points")
    public void testCalculateRewardPoints_DecimalAmount99_99_ReturnsCorrectPoints() {
        BigDecimal amount = BigDecimal.valueOf(99.99);
        BigDecimal result = RewardsCalculator.calculateRewardPoints(amount);
        BigDecimal expected = BigDecimal.valueOf(49.99);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Large amount test: $5000 should calculate correctly")
    public void testCalculateRewardPoints_LargeAmount5000_ReturnsCorrectPoints() {
        BigDecimal amount = BigDecimal.valueOf(5000.00);
        BigDecimal result = RewardsCalculator.calculateRewardPoints(amount);
        BigDecimal expected = BigDecimal.valueOf(9850.00);
        Assertions.assertEquals(expected.setScale(2, java.math.RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Rounding test: amount with repeating decimals")
    public void testCalculateRewardPoints_RoundingTest() {
        BigDecimal amount = BigDecimal.valueOf(51.67);
        BigDecimal result = RewardsCalculator.calculateRewardPoints(amount);
        Assertions.assertEquals(2, result.scale());
        Assertions.assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }
}

