package com.charter.reward.repository;


import com.charter.reward.entity.TransactionEntity;
import com.charter.reward.model.Transaction;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class TransactionRepository implements ITransactionDataSource {

    private final ITransactionJpaRepository jpaRepository;

    public TransactionRepository(ITransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public List<Transaction> findAll() {
        return jpaRepository.findAll().stream().
                map(this::mapTransactionEntityToModel)
                .toList();
    }

    public List<Transaction> findByCustomerIdAndDateBetween(@NotBlank String customerId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching transactions for customerId: {} between {} and {}", customerId, startDate, endDate);
        return jpaRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate).stream()
                .map(this::mapTransactionEntityToModel)
                .toList();
    }

    /**
     * Maps a TransactionEntity to a Transaction model object.
     */
    private Transaction mapTransactionEntityToModel(TransactionEntity entity) {
        return new Transaction(
                entity.getTransactionId(),
                entity.getCustomerId(),
                entity.getTransactionAmount(),
                entity.getCustomerName(),
                entity.getTransactionDate()
        );
    }
}