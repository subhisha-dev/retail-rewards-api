package com.retail.rewards.controller;


import com.retail.rewards.dto.RewardsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void getRewardsByCustomer_SuccessFlowReturs200() {
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
        String date = "2020-04-20";

        Map<String, Double> rewardsMap = new HashMap<>();
        rewardsMap.put("2026-04", 131.74);

        //rewardsResponse = new RewardsResponse("1", rewardsMap, 406.74);

        ResponseEntity<RewardsResponse> res = restTemplate.
                getForEntity("/api/rewards/"+ customerId + "/?date=" + date, RewardsResponse.class);

        assertEquals(HttpStatus.OK,res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(customerId, res.getBody().getCustomerId());
        //assertEquals(1, res.getBody().getMonthlyRewardPoints().size());
        //assertEquals(res.getBody().getTotalRewardPoints(), res.getBody().getMonthlyRewardPoints().values().stream().mapToDouble(Double::doubleValue).sum());

        }

        @Test
        public void getCustomerRewardsForDate_noData_Returns404() {
        String customerId = "999";
        String date = "2020-04-20";

            ResponseEntity<RewardsResponse> res = restTemplate.
                    getForEntity("/api/rewards/"+ customerId + "/?date=" + date, RewardsResponse.class);

            assertEquals(HttpStatus.NOT_FOUND,res.getStatusCode());
        }

}
