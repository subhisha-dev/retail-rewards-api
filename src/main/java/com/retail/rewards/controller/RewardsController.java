package com.retail.rewards.controller;

import com.retail.rewards.dto.RewardsResponse;
import com.retail.rewards.service.RewardsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @GetMapping("/allRewards")
    public ResponseEntity<List<RewardsResponse>> getAllRewards() {
        return ResponseEntity.ok(rewardsService.getTransactionsAllRewards());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardsResponse> getRewardsByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(rewardsService.getCustomerRewards(customerId));
    }

    @GetMapping("/{customerId}/")
    public ResponseEntity<RewardsResponse> getCustomerRewardsForDate(@PathVariable String customerId,
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        System.out.println("getCustomerRewardsForDate called:" + date);
        if(date != null) {
            return ResponseEntity.ok(rewardsService.getCustomerRewardsForDate(customerId, date));
        }
        return ResponseEntity.ok(rewardsService.getCustomerRewards(customerId));
    }

}
