package com.charter.reward.util;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * Unit tests for ValidationUtils class.
 * Tests cover customerId and date range validation logic.
 */
@DisplayName("ValidationUtils Tests")
public class ValidationUtilsTest {

    private static final String VALID_CUSTOMER_ID = "1";
    private static final String BLANK_CUSTOMER_ID = " ";
    private static final String EMPTY_CUSTOMER_ID = "";
    private static final LocalDate START_DATE = LocalDate.of(2026, 4, 1);
    private static final LocalDate END_DATE = LocalDate.of(2026, 6, 30);
    private static final String ERROR_MESSAGE = "Invalid Input";

    @Test
    @DisplayName("Valid customerId, startDate, and endDate should pass validation")
    public void testValidateCustomerAndDates_ValidInput_PassesValidation() {
        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, START_DATE, END_DATE, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("Null customerId should throw ConstraintViolationException")
    public void testValidateCustomerAndDates_NullCustomerId_ThrowsException() {
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates(null, START_DATE, END_DATE, ERROR_MESSAGE)
        );
        Assertions.assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Blank customerId should throw ConstraintViolationException")
    public void testValidateCustomerAndDates_BlankCustomerId_ThrowsException() {
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates(BLANK_CUSTOMER_ID, START_DATE, END_DATE, ERROR_MESSAGE)
        );
        Assertions.assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Empty customerId should throw ConstraintViolationException")
    public void testValidateCustomerAndDates_EmptyCustomerId_ThrowsException() {
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates(EMPTY_CUSTOMER_ID, START_DATE, END_DATE, ERROR_MESSAGE)
        );
        Assertions.assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Null startDate should throw ConstraintViolationException")
    public void testValidateCustomerAndDates_NullStartDate_ThrowsException() {
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, null, END_DATE, ERROR_MESSAGE)
        );
        Assertions.assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Null endDate should throw ConstraintViolationException")
    public void testValidateCustomerAndDates_NullEndDate_ThrowsException() {
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, START_DATE, null, ERROR_MESSAGE)
        );
        Assertions.assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Both null dates should throw ConstraintViolationException")
    public void testValidateCustomerAndDates_BothNullDates_ThrowsException() {
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, null, null, ERROR_MESSAGE)
        );
        Assertions.assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("EndDate before startDate should throw ConstraintViolationException")
    public void testValidateCustomerAndDates_EndDateBeforeStart_ThrowsException() {
        LocalDate startDate = LocalDate.of(2026, 6, 30);
        LocalDate endDate = LocalDate.of(2026, 4, 1);

        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, startDate, endDate, ERROR_MESSAGE)
        );
        Assertions.assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Same startDate and endDate should pass validation")
    public void testValidateCustomerAndDates_SameDates_PassesValidation() {
        LocalDate sameDate = LocalDate.of(2026, 5, 15);

        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, sameDate, sameDate, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("EndDate after startDate should pass validation")
    public void testValidateCustomerAndDates_EndDateAfterStart_PassesValidation() {
        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, START_DATE, END_DATE, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("Single character customerId should pass validation")
    public void testValidateCustomerAndDates_SingleCharCustomerId_PassesValidation() {
        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates("1", START_DATE, END_DATE, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("Long customerId should pass validation")
    public void testValidateCustomerAndDates_LongCustomerId_PassesValidation() {
        String longCustomerId = "CUST-12345-67890-ABCDE";

        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(longCustomerId, START_DATE, END_DATE, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("CustomerId with spaces should fail validation (isBlank checks for whitespace)")
    public void testValidateCustomerAndDates_CustomerIdWithOnlySpaces_ThrowsException() {
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates("   ", START_DATE, END_DATE, ERROR_MESSAGE)
        );
        Assertions.assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("CustomerId with leading/trailing spaces should pass (only blank strings fail)")
    public void testValidateCustomerAndDates_CustomerIdWithSpacesAroundContent_PassesValidation() {
        String customerIdWithSpaces = " 123 ";

        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(customerIdWithSpaces, START_DATE, END_DATE, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("Error message should be preserved in exception")
    public void testValidateCustomerAndDates_CustomErrorMessage_IsPreserved() {
        String customMessage = "Custom validation error for special case";

        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> ValidationUtils.validateCustomerAndDates(null, START_DATE, END_DATE, customMessage)
        );

        Assertions.assertEquals(customMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Very far future dates should pass validation")
    public void testValidateCustomerAndDates_FarFutureDates_PassesValidation() {
        LocalDate futureStart = LocalDate.of(2099, 1, 1);
        LocalDate futureEnd = LocalDate.of(2099, 12, 31);

        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, futureStart, futureEnd, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("Past dates should pass validation (no restriction on date range)")
    public void testValidateCustomerAndDates_PastDates_PassesValidation() {
        LocalDate pastStart = LocalDate.of(2020, 1, 1);
        LocalDate pastEnd = LocalDate.of(2020, 12, 31);

        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, pastStart, pastEnd, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("One day range should pass validation")
    public void testValidateCustomerAndDates_OneDayRange_PassesValidation() {
        LocalDate day = LocalDate.of(2026, 5, 15);

        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, day, day, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("Large date range should pass validation")
    public void testValidateCustomerAndDates_LargeDateRange_PassesValidation() {
        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2099, 12, 31);

        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates(VALID_CUSTOMER_ID, startDate, endDate, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("Numeric customerId string should pass validation")
    public void testValidateCustomerAndDates_NumericCustomerId_PassesValidation() {
        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates("12345", START_DATE, END_DATE, ERROR_MESSAGE)
        );
    }

    @Test
    @DisplayName("Alphanumeric customerId should pass validation")
    public void testValidateCustomerAndDates_AlphanumericCustomerId_PassesValidation() {
        Assertions.assertDoesNotThrow(() ->
                ValidationUtils.validateCustomerAndDates("CUST123", START_DATE, END_DATE, ERROR_MESSAGE)
        );
    }
}

