package com.charter.reward.controller;


import com.charter.reward.exception.CustomerNotFoundException;
import com.charter.reward.model.RewardsResponse;
import com.charter.reward.service.RewardsService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RewardsControllerTest {

    @Mock
    private RewardsService rewardsService;

    @InjectMocks
    private RewardsController rewardsController;

    private RewardsResponse rewardsResponse;

    @BeforeEach
    public void setup() {
        Map<String, java.math.BigDecimal> rewardsMap = new HashMap<>();
        rewardsMap.put("2026-04", java.math.BigDecimal.valueOf(131.74));
        rewardsMap.put("2026-05", java.math.BigDecimal.valueOf(25.0));
        rewardsMap.put("2026-06", java.math.BigDecimal.valueOf(250.0));
        rewardsResponse = new RewardsResponse("1","John",3, 4, rewardsMap, java.math.BigDecimal.valueOf(406.74), null, null);
    }

    @Test
    public void testGetRewards_HappyFlow() {
        List<RewardsResponse>  mockResponse = List.of(rewardsResponse);

        when(rewardsService.getTransactionsAllRewards()).thenReturn(mockResponse);

        ResponseEntity<List<RewardsResponse>> rewardsResponses = rewardsController.getAllRewards();

        assertEquals(200, rewardsResponses.getStatusCode().value());
        Assertions.assertNotNull(rewardsResponses.getBody());
        assertEquals(1, rewardsResponses.getBody().size());
        assertEquals("1", rewardsResponses.getBody().getFirst().getCustomerId());
        verify(rewardsService, times(1)).getTransactionsAllRewards();
    }

    @Test
    public void testGetRewards_EmptyList_ReturnsOkWithEmptyBody() {
        when(rewardsService.getTransactionsAllRewards()).thenReturn(Collections.emptyList());

        ResponseEntity<List<RewardsResponse>> rewardsResponses = rewardsController.getAllRewards();

        assertEquals(200, rewardsResponses.getStatusCode().value());
        Assertions.assertNotNull(rewardsResponses.getBody());
        Assertions.assertTrue(rewardsResponses.getBody().isEmpty());
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
    public void testGetCustomerRewardsForPeriod_HappyFlow() throws Exception {

        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);

        rewardsResponse.getMonthlyRewardPoints().put("2026-02", java.math.BigDecimal.valueOf(630.1));
        rewardsResponse.setTotalRewardPoints(java.math.BigDecimal.valueOf(1036.84));
        rewardsResponse.setStartDate(startDate);
        rewardsResponse.setEndDate(endDate);

        when(rewardsService.getCustomerRewardsForPeriod(customerId, startDate, endDate)).thenReturn(rewardsResponse);

        ResponseEntity<RewardsResponse> res = rewardsController.getCustomerRewardsForPeriod(customerId, startDate, endDate);

        assertEquals(200, res.getStatusCode().value());
        Assertions.assertNotNull(res.getBody());
        assertEquals("1", res.getBody().getCustomerId());
        assertEquals(4, res.getBody().getMonthlyRewardPoints().size());
        assertEquals(0, res.getBody().getTotalRewardPoints().compareTo(res.getBody().getMonthlyRewardPoints().values().stream().reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)));
        verify(rewardsService, times(1)).getCustomerRewardsForPeriod("1", startDate, endDate);
    }

    @Test
    public void testGetCustomerRewardsForPeriod_CustomerNotFoundExceptionFlow() throws Exception {

        String customerId = "99";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);
        when(rewardsService.getCustomerRewardsForPeriod(customerId, startDate, endDate)).thenThrow(new CustomerNotFoundException("CustomerId not found"));

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsController.getCustomerRewardsForPeriod("99", startDate, endDate);
        });

        Assertions.assertEquals("CustomerId not found", exception.getMessage());
        verify(rewardsService, times(1)).getCustomerRewardsForPeriod("99", startDate, endDate);
    }

    @Test
    public void testGetCustomerRewardsForPeriod_ConstraintViolationExceptionFlow() throws Exception {

        String customerId = " ";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            rewardsController.getCustomerRewardsForPeriod(" ", startDate, endDate);
        });

        Assertions.assertEquals("Invalid Input to the controller method", exception.getMessage());
        verify(rewardsService, times(0)).getCustomerRewardsForPeriod(anyString(), any(), any());
    }

    @Test
    public void testGetCustomerRewardsForPeriod_NullStartDate_ConstraintViolation() {
        String customerId = "1";
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2026,6,30);

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            rewardsController.getCustomerRewardsForPeriod(customerId, startDate, endDate);
        });

        Assertions.assertEquals("Invalid Input to the controller method", exception.getMessage());
        verify(rewardsService, times(0)).getCustomerRewardsForPeriod(anyString(), any(), any());
    }

    @Test
    public void testGetCustomerRewardsForPeriod_NullEndDate_ConstraintViolation() {
        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = null;

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            rewardsController.getCustomerRewardsForPeriod(customerId, startDate, endDate);
        });

        Assertions.assertEquals("Invalid Input to the controller method", exception.getMessage());
        verify(rewardsService, times(0)).getCustomerRewardsForPeriod(anyString(), any(), any());
    }

    @Test
    public void testGetCustomerRewardsForPeriod_EndDateBeforeStart_ConstraintViolation() {
        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026,6,30);
        LocalDate endDate = LocalDate.of(2026,4,9);

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            rewardsController.getCustomerRewardsForPeriod(customerId, startDate, endDate);
        });

        Assertions.assertEquals("Invalid Input to the controller method", exception.getMessage());
        verify(rewardsService, times(0)).getCustomerRewardsForPeriod(anyString(), any(), any());
    }

}
