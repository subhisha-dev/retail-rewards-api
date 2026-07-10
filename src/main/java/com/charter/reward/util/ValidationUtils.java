package com.charter.reward.util;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;

@Slf4j
public class ValidationUtils {

    /**
     * Validates customerId, startDate, and endDate. Throws ConstraintViolationException with the provided message
     * when any validation fails.
     */
    public static void validateCustomerAndDates(String customerId, LocalDate startDate, LocalDate endDate, String message) {
        log.debug("Executing validateCustomerAndDates method for customer:{} for dates {} - {}", customerId, startDate, endDate);
        if (customerId == null || customerId.isBlank() || startDate == null || endDate == null) {
            throw new ConstraintViolationException(message, new HashSet<>());
        }
        if (endDate.isBefore(startDate)) {
            throw new ConstraintViolationException(message, new HashSet<>());
        }
    }
}

