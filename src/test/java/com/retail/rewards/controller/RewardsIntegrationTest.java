package com.retail.rewards.controller;


import com.retail.rewards.dto.RewardsResponse;
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
        assertTrue(res.getBody().getTotalRewardPoints() >=0);
    }

    @Test
    public void getRewardsByCustomer_404_Returns404() {
        String customerId = "999";

        ResponseEntity<RewardsResponse> res = restTemplate.getForEntity( "/api/rewards/"+ customerId, RewardsResponse.class);
        assertEquals(HttpStatus.NOT_FOUND,res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    public void getCustomerRewardsForDate_validDate_ReturnsOnlyThatMonthRewards()
        throws Exception {
        String customerId = "1";
        String date = "2026-04-20";

        String url = "/api/rewards/byDate/"+ customerId + "?date=" + date;
        System.out.println(url);

        ResponseEntity<RewardsResponse> res = restTemplate.
                getForEntity(url, RewardsResponse.class);

        assertEquals(HttpStatus.OK,res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(customerId, res.getBody().getCustomerId());
        assertEquals(1, res.getBody().getMonthlyRewardPoints().size());
        assertEquals(res.getBody().getTotalRewardPoints(), res.getBody().getMonthlyRewardPoints().values().stream().mapToDouble(Double::doubleValue).sum());

        }

        @Test
        public void getCustomerRewardsForDate_noData_Returns404() {
        String customerId = "999";
        String date = "2020-04-20";

            ResponseEntity<RewardsResponse> res = restTemplate.
                    getForEntity("/api/rewards/byDate/"+ customerId + "?date=" + date, RewardsResponse.class);

            assertEquals(HttpStatus.NOT_FOUND,res.getStatusCode());
        }

    @Test
    public void getCustomerRewardsForMonths_SuccessFlow_ReturnsPastMonthsRewards()
            throws Exception {
        String customerId = "1";
        int months = 3;

        String url = "/api/rewards/byMonths/"+ customerId + "?months=" + months;
        System.out.println(url);

        ResponseEntity<RewardsResponse> res = restTemplate.
                getForEntity(url, RewardsResponse.class);

        assertEquals(HttpStatus.OK,res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(customerId, res.getBody().getCustomerId());
        assertEquals(3, res.getBody().getMonthlyRewardPoints().size());
        assertEquals(res.getBody().getTotalRewardPoints(), res.getBody().getMonthlyRewardPoints().values().stream().mapToDouble(Double::doubleValue).sum());

    }

    @Test
    public void getCustomerRewardsForMonths_noData_Returns404() {
        String customerId = "99";
        int months = 3;

        ResponseEntity<RewardsResponse> res = restTemplate.
                getForEntity("/api/rewards/byMonths/"+ customerId + "?months=" + months, RewardsResponse.class);

        assertEquals(HttpStatus.NOT_FOUND,res.getStatusCode());
    }

    @Test
    public void getCustomerRewardsForMonths_InvalidInput_Returns400() {
        String customerId = " ";
        int months = 3;

        ResponseEntity<RewardsResponse> res = restTemplate.
                getForEntity("/api/rewards/byMonths/"+ customerId + "?months=" + months, RewardsResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
    }
}
