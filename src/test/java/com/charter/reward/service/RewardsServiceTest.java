package com.charter.reward.service;

import com.charter.reward.exception.CustomerNotFoundException;
import com.charter.reward.model.MonthlyReward;
import com.charter.reward.model.RewardsResponse;
import com.charter.reward.model.Transaction;
import com.charter.reward.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RewardsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardsService rewardsService;

    private List<Transaction> transactionList;

    @BeforeEach
    public void setup() {

        transactionList = Arrays.asList(
                new Transaction("T1", "1", BigDecimal.valueOf(120.87), "John",LocalDate.of(2026,04,10 )),
                new Transaction("T2", "1", BigDecimal.valueOf(90.0), "John", LocalDate.of(2026,04,15)),
                new Transaction("T3", "1", BigDecimal.valueOf(75.0), "John",  LocalDate.of(2026,05,20)),
                new Transaction("T4", "1", BigDecimal.valueOf(200.0), "John",  LocalDate.of(2026,06,10)));
    }

    private BigDecimal getMonthlyPoints(RewardsResponse res, int year, String month) {
        return res.getMonthlyRewardPoints().stream()
                .filter(m -> m.getYear() == year && m.getMonth().equals(month))
                .map(MonthlyReward::getRewardPoints)
                .findFirst().orElse(BigDecimal.ZERO);
    }

    @Test
    public void testGetRewards_SuccessFlow() {
        // when
        when(transactionRepository.findAll()).thenReturn(transactionList);

        List<RewardsResponse> rewardsResponses = rewardsService.getTransactionsAllRewards();

        // then
        Assertions.assertNotNull(rewardsResponses);
        assertEquals("1", rewardsResponses.getFirst().getCustomerId());
        assertEquals(4, rewardsResponses.getFirst().getTransactions().size()); // 5. NEW
        assertEquals(BigDecimal.valueOf(131.74), getMonthlyPoints(rewardsResponses.getFirst(), 2026, "APRIL")); // 6. UPDATED
        assertEquals(BigDecimal.valueOf(406.74), rewardsResponses.getFirst().getTotalRewardPoints());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetRewards_EmptyRepositoryResult_ReturnsEmptyList() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        List<RewardsResponse> rewardsResponses = rewardsService.getTransactionsAllRewards();

        Assertions.assertNotNull(rewardsResponses);
        Assertions.assertTrue(rewardsResponses.isEmpty());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetRewards_NullRepositoryResult_ReturnsEmptyList() {
        when(transactionRepository.findAll()).thenReturn(null);

        List<RewardsResponse> rewardsResponses = rewardsService.getTransactionsAllRewards();

        Assertions.assertNotNull(rewardsResponses);
        Assertions.assertTrue(rewardsResponses.isEmpty());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetRewards_MultipleCustomers_GroupsRewardsByCustomer() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("T1", "1", BigDecimal.valueOf(120.87), "John", LocalDate.of(2026, 4, 10)),
                new Transaction("T2", "1", BigDecimal.valueOf(90.0), "John", LocalDate.of(2026, 4, 15)),
                new Transaction("T3", "2", BigDecimal.valueOf(200.0), "Alex", LocalDate.of(2026, 6, 25))
        );

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<RewardsResponse> rewardsResponses = rewardsService.getTransactionsAllRewards();

        Assertions.assertNotNull(rewardsResponses);
        assertEquals(2, rewardsResponses.size());
        Assertions.assertTrue(rewardsResponses.stream().anyMatch(response -> "1".equals(response.getCustomerId())));
        Assertions.assertTrue(rewardsResponses.stream().anyMatch(response -> "2".equals(response.getCustomerId())));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetRewards_FiltersInvalidTransactions() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("T1", "1", BigDecimal.valueOf(120.87), "John", LocalDate.of(2026, 4, 10)),
                new Transaction("T2", null, BigDecimal.valueOf(90.0), "John", LocalDate.of(2026, 4, 15)),
                new Transaction("T3", "2", null, "Alex", LocalDate.of(2026, 6, 25)),
                new Transaction("T4", "3", BigDecimal.valueOf(200.0), "Alice", null),
                null
        );

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<RewardsResponse> rewardsResponses = rewardsService.getTransactionsAllRewards();

        Assertions.assertNotNull(rewardsResponses);
        assertEquals(1, rewardsResponses.size());
        assertEquals("1", rewardsResponses.getFirst().getCustomerId());
        assertEquals(1, rewardsResponses.getFirst().getTotalTransactions());
        assertEquals(1, rewardsResponses.getFirst().getTransactions().size()); // 5. NEW
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetRewards_HappyFlow_IfTransactionFileNotLoaded() throws IllegalStateException {

        when(transactionRepository.findAll()).thenThrow(new IllegalStateException("Could not initialize transaction data"));
        IllegalStateException exception  = Assertions.assertThrows(IllegalStateException.class, () -> {
            rewardsService.getTransactionsAllRewards();
        });

        assertEquals("Could not initialize transaction data", exception.getMessage());
        verify(transactionRepository, times(1)).findAll();

    }

    @Test
    public void testGetRewardsByCustomer_HappyFlow() {
        String customerId = "1";
        when(transactionRepository.findAll()).thenReturn(transactionList);
        rewardsService.getCustomerRewards(customerId);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetTransactionsForCustomer_ReturnsOnlyRequestedCustomerTransactions() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("T1", "1", BigDecimal.valueOf(120.87), "John", LocalDate.of(2026, 4, 10)),
                new Transaction("T2", "1", BigDecimal.valueOf(90.0), "John", LocalDate.of(2026, 4, 15)),
                new Transaction("T3", "2", BigDecimal.valueOf(200.0), "Alex", LocalDate.of(2026, 6, 25))
        );

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> customerTransactions = rewardsService.getTransactionsForCustomer("1");

        Assertions.assertNotNull(customerTransactions);
        assertEquals(2, customerTransactions.size());
        Assertions.assertTrue(customerTransactions.stream().allMatch(transaction -> "1".equals(transaction.getCustomerId())));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetRewardsByCustomer_ExceptionFlow_CustomerNotFound() throws CustomerNotFoundException {
        when(transactionRepository.findAll()).thenReturn(new ArrayList<>());

        CustomerNotFoundException exception  = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.getCustomerRewards("6");
        });

        Assertions.assertEquals("Customer ID:6 not found", exception.getMessage());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetCustomerRewards_IncludesCustomerDetails() throws Exception {
        String customerId = "1";
        when(transactionRepository.findAll()).thenReturn(transactionList);

        RewardsResponse res = rewardsService.getCustomerRewards(customerId);

        Assertions.assertNotNull(res);
        assertEquals("1", res.getCustomerId());
        assertEquals("John", res.getCustomerName());
        assertEquals(4, res.getTotalTransactions());
        assertEquals(3, res.getMonthlyRewardPoints().size());
        assertEquals(4, res.getTransactions().size());
        Assertions.assertNull(res.getStartDate());
        Assertions.assertNull(res.getEndDate());
    }

    @Test
    public void testGetCustomerRewardsForPeriod_HappyFlow() throws Exception {

        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);

        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate)).thenReturn(transactionList);

        RewardsResponse res = rewardsService.getCustomerRewardsForPeriod(customerId, startDate, endDate);

        Assertions.assertNotNull(res);
        assertEquals("1", res.getCustomerId());
        assertEquals(3, res.getMonthlyRewardPoints().size());
        assertEquals(BigDecimal.valueOf(131.74), getMonthlyPoints(res, 2026, "APRIL"));
        assertEquals(406.74, res.getTotalRewardPoints().doubleValue());
        verify(transactionRepository, times(1)).findByCustomerIdAndDateBetween(customerId, startDate, endDate);
    }

    @Test
    public void testGetCustomerRewardsForPeriod_IncludesCustomerAndPeriodDetails() throws Exception {
        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);

        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate)).thenReturn(transactionList);

        RewardsResponse res = rewardsService.getCustomerRewardsForPeriod(customerId, startDate, endDate);

        Assertions.assertNotNull(res);
        assertEquals("1", res.getCustomerId());
        assertEquals("John", res.getCustomerName());
        assertEquals(4, res.getTotalTransactions());
        assertEquals(3, res.getMonthlyRewardPoints().size());
        assertEquals(startDate, res.getStartDate());
        assertEquals(endDate, res.getEndDate());
        assertEquals(3, res.getPeriodInMonths());
        assertEquals(4, res.getTransactions().size()); // 5. NEW
        verify(transactionRepository, times(1)).findByCustomerIdAndDateBetween(customerId, startDate, endDate);
    }

    @Test
    public void testGetCustomerRewardsForPeriod_ExceptionFlow_NoTransactions_CustomerNotFoundException() throws Exception {

        String customerId = "99";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);

        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate)).thenReturn(new ArrayList<>());

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.getCustomerRewardsForPeriod("99", startDate, endDate);
        });

        Assertions.assertEquals("No valid transactions found for CustomerID:99 for the provided period", exception.getMessage());
        verify(transactionRepository, times(1)).findByCustomerIdAndDateBetween(customerId, startDate, endDate);
    }

    @Test
    public void testGetCustomerRewardsForPeriod_NullRepositoryResult_ThrowsCustomerNotFoundException() {
        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);

        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate)).thenReturn(null);

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.getCustomerRewardsForPeriod(customerId, startDate, endDate);
        });

        Assertions.assertEquals("No valid transactions found for CustomerID:1 for the provided period", exception.getMessage());
        verify(transactionRepository, times(1)).findByCustomerIdAndDateBetween(customerId, startDate, endDate);
    }

    @Test
    public void testGetCustomerRewardsForPeriod_RepositoryException_PropagatesException() {
        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);

        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate))
                .thenThrow(new IllegalStateException("Database unavailable"));

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            rewardsService.getCustomerRewardsForPeriod(customerId, startDate, endDate);
        });

        Assertions.assertEquals("Database unavailable", exception.getMessage());
        verify(transactionRepository, times(1)).findByCustomerIdAndDateBetween(customerId, startDate, endDate);
    }

    @Test
    public void testGetCustomerRewardsForPeriod_ExceptionFlow_InvalidInput_ConstraintViolationException() throws Exception {

        String customerId = "null";
        LocalDate startDate = LocalDate.of(2026,4,9);
        LocalDate endDate = LocalDate.of(2026,6,30);

        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate)).thenThrow(new CustomerNotFoundException("Customer ID:null"));

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.getCustomerRewardsForPeriod("null", startDate, endDate);
        });

        Assertions.assertEquals("Customer ID:null", exception.getMessage());
        verify(transactionRepository, times(1)).findByCustomerIdAndDateBetween(customerId, startDate, endDate);
    }
}