package com.charter.reward.repository;

import com.charter.reward.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


/**
 * Spring Data JPA Repository for TransactionEntity.
 * Provides database access operations with automatic query generation.
 */
@Repository
public interface ITransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    /**
     * Find all transactions for a specific customer within a date range.
     */
    List<TransactionEntity> findByCustomerIdAndTransactionDateBetween(String customerId, LocalDate startDate, LocalDate endDate);
}