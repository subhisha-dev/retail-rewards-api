package com.retail.rewards.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.retail.rewards.dto.RewardsResponse;
import com.retail.rewards.exception.CustomerNotFoundException;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.service.RewardsService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RewardsControllerTest {

    @Mock
    private RewardsService rewardsService;

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    private RewardsController rewardsController;

    private RewardsResponse rewardsResponse;

    @BeforeEach
    public void setup() {
        Map<String, Double> rewardsMap = new HashMap<>();
        rewardsMap.put("2026-04", 131.74);
        rewardsMap.put("2026-05", 25.0);
        rewardsMap.put("2026-06", 250.0);
        rewardsResponse = new RewardsResponse("1",rewardsMap, 406.74);
    }

    @Test
    public void testGetRewards_HappyFlow() {
        List<RewardsResponse>  mockResponse = List.of(rewardsResponse);
        // when
        when(rewardsService.getTransactionsAllRewards()).thenReturn(mockResponse);

        ResponseEntity<List<RewardsResponse>> rewardsResponses = rewardsController.getAllRewards();

        // then
        assertEquals(200, rewardsResponses.getStatusCode().value());
        Assertions.assertNotNull(rewardsResponses.getBody());
        assertEquals(1, rewardsResponses.getBody().size());
        assertEquals("1", rewardsResponses.getBody().getFirst().getCustomerId());
        verify(rewardsService, times(1)).getTransactionsAllRewards();
    }

    @Test
    public void testGetRewards_ExceptionFlow_NoTransactions() throws IllegalStateException {

        when(rewardsService.getTransactionsAllRewards()).thenThrow(new IllegalStateException("Could not initialize transaction data"));
        IllegalStateException exception  = Assertions.assertThrows(IllegalStateException.class, () -> {
            rewardsController.getAllRewards();
        });

        assertEquals("Could not initialize transaction data", exception.getMessage());
        verify(rewardsService, times(1)).getTransactionsAllRewards();

    }

    @Test
    public void testGetRewardsByCustomer_HappyFlow() {
        String customerId = "1";
        rewardsController.getRewardsByCustomer(customerId);
        verify(rewardsService, times(1)).getCustomerRewards(customerId);
    }

    @Test
    public void testGetRewardsByCustomer_ExceptionFlow_CustomerNotFound() throws CustomerNotFoundException {
        when(rewardsService.getCustomerRewards(anyString())).thenThrow(new CustomerNotFoundException("CustomerId not found"));

        CustomerNotFoundException exception  = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsController.getRewardsByCustomer("6");
        });

        Assertions.assertEquals("CustomerId not found", exception.getMessage());
        verify(rewardsService, times(1)).getCustomerRewards(anyString());
    }

    @Test
    public void testGetCustomerRewardsForDate_HappyFlow() throws Exception {

        String customerId = "1";
        LocalDate localDate = LocalDate.of(2026, 04,10);
        rewardsResponse.getMonthlyRewardPoints().remove("2026-05");
        rewardsResponse.getMonthlyRewardPoints().remove("2026-06");

        when(rewardsService.getCustomerRewardsForDate(customerId, localDate)).thenReturn(rewardsResponse);


        //when(transactionRepository.findAll()).thenReturn(List.of(new Transaction("T1","1",120.87, localDate)));
        ResponseEntity<RewardsResponse> res = rewardsController.getCustomerRewardsForDate(customerId, localDate);

        // 31 * 2 = 62 + 50 = 112
        assertEquals(200, res.getStatusCode().value());
        Assertions.assertNotNull(res.getBody());
        assertEquals("1", res.getBody().getCustomerId());
        assertEquals(1, res.getBody().getMonthlyRewardPoints().size());
        assertEquals(131.74, res.getBody().getMonthlyRewardPoints().get("2026-04"));
        assertEquals(406.74, res.getBody().getTotalRewardPoints());
        verify(rewardsService, times(1)).getCustomerRewardsForDate(any(), any());
    }

    @Test
    public void testGetCustomerRewardsForDate_HappyFlow_IfDateIsNull() throws Exception {

        String customerId = "1";

        when(rewardsService.getCustomerRewards(customerId)).thenReturn(rewardsResponse);

        ResponseEntity<RewardsResponse> res = rewardsController.getCustomerRewardsForDate(customerId, null);

        assertEquals(200, res.getStatusCode().value());
        Assertions.assertNotNull(res.getBody());
        assertEquals("1", res.getBody().getCustomerId());
        assertEquals(3, res.getBody().getMonthlyRewardPoints().size());
        assertEquals(131.74, res.getBody().getMonthlyRewardPoints().get("2026-04"));
        assertEquals(406.74, res.getBody().getTotalRewardPoints());
        verify(rewardsService, times(1)).getCustomerRewards(any());
    }

    @Test
    public void testGetCustomerRewardsForDate_Exception() throws CustomerNotFoundException {
        when(rewardsService.getCustomerRewardsForDate(anyString(), any())).thenThrow(new CustomerNotFoundException("CustomerId not found"));

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsController.getCustomerRewardsForDate("6", LocalDate.of(2026, 04,10));
        });

        Assertions.assertEquals("CustomerId not found", exception.getMessage());
        verify(rewardsService, times(1)).getCustomerRewardsForDate(anyString(), any());
    }

    @Test
    public void testGetCustomerRewardsForDateNull_Exception() throws CustomerNotFoundException {
        when(rewardsService.getCustomerRewards(anyString())).thenThrow(new CustomerNotFoundException("CustomerId not found"));

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsController.getCustomerRewardsForDate("6", null);
        });

        Assertions.assertEquals("CustomerId not found", exception.getMessage());
        verify(rewardsService, times(1)).getCustomerRewards(anyString());
    }
}
