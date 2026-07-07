package com.retail.rewards.controller;

import com.retail.rewards.dto.RewardsResponse;
import com.retail.rewards.service.RewardsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
     * @return
     */
    @GetMapping("/allRewards")
    public ResponseEntity<List<RewardsResponse>> getAllRewards() {
        log.info("Executing getAllRewards method:");
        return ResponseEntity.ok(rewardsService.getTransactionsAllRewards());
    }

    /**
     * This method is used for calculating the reward points for a customer by passing a customer id as a param
     *
     * @param customerId
     * @return
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<RewardsResponse> getRewardsByCustomer(@PathVariable @NotBlank String customerId) {
        log.info("Executing getRewardsByCustomer method for Customer ID: {}:", customerId);
        return ResponseEntity.ok(rewardsService.getCustomerRewards(customerId));
    }

    /**
     * Method used to calculate the reward points of a customer on the specified month by passing
     * the customerId and the date as parameters.
     *
     * @param customerId
     * @param date
     * @return
     */
    @GetMapping("byDate/{customerId}")
    public ResponseEntity<RewardsResponse> getCustomerRewardsForDate(@PathVariable @NotBlank String customerId,
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Executing getCustomerRewardsForDate method for Customer ID: {} and date: {}", customerId, date);
        if(date != null) {
            return ResponseEntity.ok(rewardsService.getCustomerRewardsForDate(customerId, date));
        }
        return ResponseEntity.ok(rewardsService.getCustomerRewards(customerId));
    }


    /**
     * Method used to calculate the reward points of a customer for last 3 months.
     *
     * @param customerId
     * @param months
     * @return
     */
    @GetMapping("byMonths/{customerId}")
    public ResponseEntity<RewardsResponse> getCustomerRewardsForMonths(@PathVariable @NotBlank String customerId,
                                                                     @RequestParam(defaultValue="3") @Min(1) @Max(12) int months) {
        log.info("Executing getCustomerRewardsForMonths method: {}", months);
        return ResponseEntity.ok(rewardsService.getCustomerRewardsForMonths(customerId, months));
    }

}
