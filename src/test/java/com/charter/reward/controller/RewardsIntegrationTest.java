package com.charter.reward.controller;


import com.charter.reward.model.RewardsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RewardsIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private RewardsResponse rewardsResponse;

    @Test
    public void getAllRewards_ReturnsAllRewards()
        throws Exception {
        ResponseEntity<List> res = restTemplate.getForEntity("/api/rewards/allRewards", List.class);

        assertEquals(HttpStatus.OK,res.getStatusCode());
        assertNotNull(res.getBody());
        assertFalse(res.getBody().isEmpty());
    }

    @Test
    public void getRewardsByCustomer_SuccessFlowReturns200() {
        String customerId = "1";

        ResponseEntity<RewardsResponse> res = restTemplate.getForEntity("/api/rewards/"+ customerId, RewardsResponse.class);

        assertEquals(HttpStatus.OK,res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(customerId, res.getBody().getCustomerId());
        assertNotNull(res.getBody().getMonthlyRewardPoints());
        assertTrue(res.getBody().getTotalRewardPoints().compareTo(java.math.BigDecimal.ZERO) >= 0);
    }

    @Test
    public void getRewardsByCustomer_404_Returns404() {
        String customerId = "999";

        ResponseEntity<RewardsResponse> res = restTemplate.getForEntity( "/api/rewards/"+ customerId, RewardsResponse.class);
        assertEquals(HttpStatus.NOT_FOUND,res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    public void getCustomerRewardsForPeriod_SuccessFlow_ReturnsRewardsForRange()
            throws Exception {
        String customerId = "1";
        String startDate = "2026-04-09";
        String endDate = "2026-06-30";

        String url = "/api/rewards/byPeriod/"+ customerId + "?startDate=" + startDate + "&endDate=" + endDate;

        ResponseEntity<RewardsResponse> res = restTemplate.getForEntity(url, RewardsResponse.class);

        assertEquals(HttpStatus.OK,res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(customerId, res.getBody().getCustomerId());
        assertFalse(res.getBody().getMonthlyRewardPoints().isEmpty());
        assertEquals(0, res.getBody().getTotalRewardPoints().compareTo(res.getBody().getMonthlyRewardPoints().values().stream().reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)));
    }

    @Test
    public void getCustomerRewardsForMonths_noData_Returns404() {
        String customerId = "99";
        String startDate = "2026-04-09";
        String endDate = "2026-06-30";

        ResponseEntity<RewardsResponse> res = restTemplate.getForEntity("/api/rewards/byPeriod/"+ customerId + "?startDate=" + startDate + "&endDate=" + endDate, RewardsResponse.class);

        assertEquals(HttpStatus.NOT_FOUND,res.getStatusCode());
    }

    @Test
    public void getCustomerRewardsForMonths_InvalidInput_Returns400() {
        String customerId = " ";
        String startDate = "2026-04-09";
        String endDate = "2026-06-30";

        ResponseEntity<RewardsResponse> res = restTemplate.getForEntity("/api/rewards/byPeriod/"+ customerId + "?startDate=" + startDate + "&endDate=" + endDate, RewardsResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
    }
}
