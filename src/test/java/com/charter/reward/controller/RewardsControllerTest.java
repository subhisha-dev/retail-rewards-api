package com.charter.reward.controller;


import com.charter.reward.exception.CustomerNotFoundException;
import com.charter.reward.model.MonthlyReward;
import com.charter.reward.model.RewardsResponse;
import com.charter.reward.model.TransactionDetail;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
        List<MonthlyReward> monthlyRewards = List.of(
                new MonthlyReward(2026, "APRIL", BigDecimal.valueOf(131.74)),
                new MonthlyReward(2026, "MAY", BigDecimal.valueOf(25.0)),
                new MonthlyReward(2026, "JUNE", BigDecimal.valueOf(250.0))
        );

        List<TransactionDetail> transactionDetails = List.of(
                new TransactionDetail("T1", LocalDate.of(2026,4,10), BigDecimal.valueOf(120.87), BigDecimal.valueOf(91.74), 2026, "APRIL"),
                new TransactionDetail("T2", LocalDate.of(2026,4,15), BigDecimal.valueOf(90.0), BigDecimal.valueOf(40.0), 2026, "APRIL"),
                new TransactionDetail("T3", LocalDate.of(2026,5,20), BigDecimal.valueOf(75.0), BigDecimal.valueOf(25.0), 2026, "MAY"),
                new TransactionDetail("T4", LocalDate.of(2026,6,10), BigDecimal.valueOf(200.0), BigDecimal.valueOf(250.0), 2026, "JUNE")
        );

        rewardsResponse = new RewardsResponse();
        rewardsResponse.setCustomerId("1");
        rewardsResponse.setCustomerName("John");
        rewardsResponse.setPeriodInMonths(3);
        rewardsResponse.setTotalTransactions(4);
        rewardsResponse.setTransactions(transactionDetails);
        rewardsResponse.setMonthlyRewardPoints(monthlyRewards);
        rewardsResponse.setTotalRewardPoints(BigDecimal.valueOf(406.74));
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
        assertEquals(91.74, rewardsResponses.getBody().getFirst().getTransactions().getFirst().getRewardPoints().doubleValue());
        assertEquals(4, rewardsResponses.getBody().getFirst().getTotalTransactions());
        assertEquals(406.74, rewardsResponses.getBody().getFirst().getTotalRewardPoints().doubleValue());
        assertEquals(3, rewardsResponses.getBody().getFirst().getMonthlyRewardPoints().size());
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

        rewardsResponse.setStartDate(startDate);
        rewardsResponse.setEndDate(endDate);

        when(rewardsService.getCustomerRewardsForPeriod(customerId, startDate, endDate)).thenReturn(rewardsResponse);

        ResponseEntity<RewardsResponse> res = rewardsController.getCustomerRewardsForPeriod(customerId, startDate, endDate);

        assertEquals(200, res.getStatusCode().value());
        Assertions.assertNotNull(res.getBody());
        assertEquals("1", res.getBody().getCustomerId());
        assertEquals(3, res.getBody().getMonthlyRewardPoints().size());
        assertEquals(4, res.getBody().getTotalTransactions());
        assertEquals(BigDecimal.valueOf(406.74), res.getBody().getTotalRewardPoints());
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
