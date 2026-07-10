package com.charter.reward.repository;

import com.charter.reward.entity.TransactionEntity;
import com.charter.reward.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryTest {

    @Mock
    private ITransactionJpaRepository jpaRepository;

    @InjectMocks
    private TransactionRepository transactionRepository;

    private List<TransactionEntity> transactionEntities;

    @BeforeEach
    void setUp() {
        transactionEntities = List.of(
                new TransactionEntity(1L, "T1", "1", "John", BigDecimal.valueOf(120.87), LocalDate.of(2026, 4, 10)),
                new TransactionEntity(2L, "T2", "1", "John", BigDecimal.valueOf(90.00), LocalDate.of(2026, 5, 15)),
                new TransactionEntity(3L, "T3", "2", "Alex", BigDecimal.valueOf(200.00), LocalDate.of(2026, 6, 25))
        );
    }

    @Test
    void findAll_WhenEntitiesExist_ReturnsMappedTransactions() {
        when(jpaRepository.findAll()).thenReturn(transactionEntities);

        List<Transaction> transactions = transactionRepository.findAll();

        assertNotNull(transactions);
        assertEquals(3, transactions.size());

        Transaction firstTransaction = transactions.getFirst();
        assertEquals("T1", firstTransaction.getTransactionId());
        assertEquals("1", firstTransaction.getCustomerId());
        assertEquals("John", firstTransaction.getCustomerName());
        assertEquals(0, BigDecimal.valueOf(120.87).compareTo(firstTransaction.getTransactionAmount()));
        assertEquals(LocalDate.of(2026, 4, 10), firstTransaction.getTransactionDate());

        Transaction thirdTransaction = transactions.get(2);
        assertEquals("T3", thirdTransaction.getTransactionId());
        assertEquals("2", thirdTransaction.getCustomerId());
        assertEquals("Alex", thirdTransaction.getCustomerName());
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(thirdTransaction.getTransactionAmount()));
        assertEquals(LocalDate.of(2026, 6, 25), thirdTransaction.getTransactionDate());

        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenNoEntitiesExist_ReturnsEmptyList() {
        when(jpaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Transaction> transactions = transactionRepository.findAll();

        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenJpaRepositoryThrowsException_PropagatesException() {
        when(jpaRepository.findAll()).thenThrow(new IllegalStateException("Database unavailable"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> transactionRepository.findAll()
        );

        assertEquals("Database unavailable", exception.getMessage());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    void findByCustomerIdAndDateBetween_WhenValidInput_ReturnsMappedTransactions() {
        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026, 4, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 31);

        when(jpaRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate))
                .thenReturn(transactionEntities.subList(0, 2));

        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate);

        assertNotNull(transactions);
        assertEquals(2, transactions.size());

        Transaction firstTransaction = transactions.getFirst();
        assertEquals("T1", firstTransaction.getTransactionId());
        assertEquals("1", firstTransaction.getCustomerId());
        assertEquals("John", firstTransaction.getCustomerName());
        assertEquals(0, BigDecimal.valueOf(120.87).compareTo(firstTransaction.getTransactionAmount()));
        assertEquals(LocalDate.of(2026, 4, 10), firstTransaction.getTransactionDate());

        Transaction secondTransaction = transactions.get(1);
        assertEquals("T2", secondTransaction.getTransactionId());
        assertEquals("1", secondTransaction.getCustomerId());
        assertEquals("John", secondTransaction.getCustomerName());
        assertEquals(0, BigDecimal.valueOf(90.00).compareTo(secondTransaction.getTransactionAmount()));
        assertEquals(LocalDate.of(2026, 5, 15), secondTransaction.getTransactionDate());

        verify(jpaRepository, times(1)).findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
    }

    @Test
    void findByCustomerIdAndDateBetween_WhenNoMatchingEntities_ReturnsEmptyList() {
        String customerId = "99";
        LocalDate startDate = LocalDate.of(2026, 4, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);

        when(jpaRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate))
                .thenReturn(Collections.emptyList());

        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate);

        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
        verify(jpaRepository, times(1)).findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
    }

    @Test
    void findByCustomerIdAndDateBetween_WhenJpaRepositoryThrowsException_PropagatesException() {
        String customerId = "1";
        LocalDate startDate = LocalDate.of(2026, 4, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);

        when(jpaRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate))
                .thenThrow(new IllegalStateException("Database unavailable"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate)
        );

        assertEquals("Database unavailable", exception.getMessage());
        verify(jpaRepository, times(1)).findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
    }

    @Test
    void findByCustomerIdAndDateBetween_WhenCustomerIdIsBlank_DelegatesToJpaRepository() {
        String customerId = " ";
        LocalDate startDate = LocalDate.of(2026, 4, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);

        when(jpaRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate));

        verify(jpaRepository, times(1)).findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
    }
}