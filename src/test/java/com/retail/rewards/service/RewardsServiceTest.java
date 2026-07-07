package com.retail.rewards.service;

import com.retail.rewards.dto.RewardsResponse;
import com.retail.rewards.exception.CustomerNotFoundException;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.repository.TransactionRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RewardsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardsService rewardsService;

    private List<Transaction> transactionList;

    private RewardsResponse rewardsResponse;

    @BeforeEach
    public void setup() {

        Map<String, Double> rewardsMap = new HashMap<>();
        rewardsMap.put("2026-04", 131.74);
        rewardsMap.put("2026-05", 25.0);
        rewardsMap.put("2026-06", 250.0);
        rewardsResponse = new RewardsResponse("1",rewardsMap, 406.74);

        transactionList = Arrays.asList(new Transaction("T1", "1", 120.87, "John",LocalDate.of(2026,04,10 )),
                new Transaction("T2", "1", 90.0, "John", LocalDate.of(2026,04,15)),
                new Transaction("T3", "1", 75.0, "John",  LocalDate.of(2026,05,20)),
                new Transaction("T4", "1", 200.0, "John",  LocalDate.of(2026,06,10)));
    }

    @Test
    public void testGetRewards_SuccessFlow() {
        // when
        when(transactionRepository.findAll()).thenReturn(transactionList);

        List<RewardsResponse> rewardsResponses = rewardsService.getTransactionsAllRewards();

        // then
        Assertions.assertNotNull(rewardsResponses);
        assertEquals("1", rewardsResponses.getFirst().getCustomerId());
        assertEquals(131.74, rewardsResponses.getFirst().getMonthlyRewardPoints().get("2026-04"));
        assertEquals(406.74, rewardsResponses.getFirst().getTotalRewardPoints());
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
    public void testGetRewardsByCustomer_ExceptionFlow_CustomerNotFound() throws CustomerNotFoundException {
        when(transactionRepository.findAll()).thenReturn(new ArrayList<>());

        CustomerNotFoundException exception  = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.getCustomerRewards("6");
        });

        Assertions.assertEquals("Customer ID:6 not found", exception.getMessage());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetCustomerRewardsForDate_HappyFlow() throws Exception {

        String customerId = "1";
        LocalDate localDate = LocalDate.of(2026, 04,10);


        when(transactionRepository.findAll()).thenReturn(transactionList);

        //when(transactionRepository.findAll()).thenReturn(List.of(new Transaction("T1","1",120.87, localDate)));
        RewardsResponse res = rewardsService.getCustomerRewardsForDate(customerId, localDate);

        Assertions.assertNotNull(res);
        assertEquals("1", res.getCustomerId());
        assertEquals(1, res.getMonthlyRewardPoints().size());
        assertEquals(131.74, res.getMonthlyRewardPoints().get("2026-04"));
        assertEquals(131.74, res.getTotalRewardPoints());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetCustomerRewardsForDate_HappyFlow_IfDateIsNull() throws Exception {

        String customerId = "1";

        when(transactionRepository.findAll()).thenReturn(transactionList);

        RewardsResponse res = rewardsService.getCustomerRewards(customerId);

        Assertions.assertNotNull(res);
        assertEquals("1", res.getCustomerId());
        assertEquals(3, res.getMonthlyRewardPoints().size());
        assertEquals(131.74, res.getMonthlyRewardPoints().get("2026-04"));
        assertEquals(406.74, res.getTotalRewardPoints());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetCustomerRewardsForDateNull_Exception() throws CustomerNotFoundException {

        when(transactionRepository.findAll()).thenReturn(new ArrayList<>());

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.getCustomerRewardsForDate("6", LocalDate.of(2026, 04,10));
        });

        Assertions.assertEquals("Customer ID:6 not found", exception.getMessage());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetCustomerRewardsForMonths_HappyFlow() throws Exception {

        String customerId = "1";
        int months = 3;

        when(transactionRepository.findByCustomerIdAndDateAfter(customerId, LocalDate.of(2026,04,07))).thenReturn(transactionList);

        RewardsResponse res = rewardsService.getCustomerRewardsForMonths(customerId, months);

        Assertions.assertNotNull(res);
        assertEquals("1", res.getCustomerId());
        assertEquals(3, res.getMonthlyRewardPoints().size());
        assertEquals(131.74, res.getMonthlyRewardPoints().get("2026-04"));
        assertEquals(res.getTotalRewardPoints(), res.getMonthlyRewardPoints().values().stream().mapToDouble(Double::doubleValue).sum());
        verify(transactionRepository, times(1)).findByCustomerIdAndDateAfter(customerId, LocalDate.of(2026,04,07));
    }

    @Test
    public void testGetCustomerRewardsForMonths_ExceptionFlow_NoTransactions_CustomerNotFoundException() throws Exception {

        String customerId = "99";


        when(transactionRepository.findByCustomerIdAndDateAfter(customerId, LocalDate.of(2026,04,07))).thenReturn(new ArrayList<>());

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.getCustomerRewardsForMonths("99", 3);
        });

        Assertions.assertEquals("No transactions found for CustomerID:99 for last month:3", exception.getMessage());
        verify(transactionRepository, times(1)).findByCustomerIdAndDateAfter(customerId, LocalDate.of(2026,04,07));
    }

    @Test
    public void testGetCustomerRewardsForMonths_ExceptionFlow_InvalidInput_ConstraintViolationException() throws Exception {

        String customerId = "null";
        int months = 3;

        when(transactionRepository.findByCustomerIdAndDateAfter(customerId, LocalDate.of(2026,04,07))).thenThrow(new CustomerNotFoundException("Customer ID:null"));

        CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.getCustomerRewardsForMonths("null", 3);
        });

        Assertions.assertEquals("Customer ID:null", exception.getMessage());
        verify(transactionRepository, times(1)).findByCustomerIdAndDateAfter(customerId, LocalDate.of(2026,04,07));
    }
}
