package com.charter.reward.controller;

import com.charter.reward.exception.CustomerNotFoundException;
import com.charter.reward.model.RewardsResponse;
import com.charter.reward.service.RewardsService;
import com.charter.reward.util.ValidationUtils;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@Slf4j
@Validated
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    /**
     * Controller method to calculate the rewards for all the customers
     *
     * @return ResponseEntity with the list of rewards for all customers
     * @throws ConstraintViolationException if there are any validation errors
     */
    @GetMapping("/allRewards")
    public ResponseEntity<List<RewardsResponse>> getAllRewards() {
        log.info("Executing getAllRewards method:");
        return ResponseEntity.ok(rewardsService.getTransactionsAllRewards());
    }

    /**
     * This method is used for calculating the reward points for a customer by passing a customer id as a param
     *
     * @param customerId - Customer ID for which the reward points are to be calculated
     * @return ResponseEntity with the reward points for the customer
     * @throws ConstraintViolationException if the customerId is blank
     * @throws CustomerNotFoundException if the customerId is not found in the transaction data
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<RewardsResponse> getRewardsByCustomer(@PathVariable @NotBlank String customerId) {
        log.info("Executing getRewardsByCustomer method for Customer ID: {}:", customerId);
        return ResponseEntity.ok(rewardsService.getCustomerRewards(customerId));
    }

    /**
     * Method used to calculate the reward points of a customer for a provided date range.
     *
     * @param customerId - Customer ID for which the reward points are to be calculated
     * @param startDate (inclusive) in ISO date format (yyyy-MM-dd)
     * @param endDate   (inclusive) in ISO date format (yyyy-MM-dd)
     * @return ResponseEntity with the reward points for the customer for the provided date range
     * @throws ConstraintViolationException if the customerId is blank or if the startDate is invalid
     * @throws CustomerNotFoundException if the customerId is not found in the transaction data
     */
    @GetMapping("byPeriod/{customerId}")
    public ResponseEntity<RewardsResponse> getCustomerRewardsForPeriod(@PathVariable @NotBlank String customerId,
                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Executing getCustomerRewardsForPeriod method for customer:{} for dates {} - {}", customerId, startDate, endDate);

        ValidationUtils.validateCustomerAndDates(customerId, startDate, endDate, "Invalid Input to the controller method");

        return ResponseEntity.ok(rewardsService.getCustomerRewardsForPeriod(customerId, startDate, endDate));
    }

}

