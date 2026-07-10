package com.charter.reward.repository;

import com.charter.reward.model.Transaction;

import java.time.LocalDate;
import java.util.List;


/**
 * Interface defining the contract for transaction data sources.
 * This allows easy switching between different implementations (H2, MySQL, etc.)
 * without changing the service layer.
 */
public interface ITransactionDataSource {

    /**
     * Retrieve all transactions from the data source.
     *
     * @return List of all transactions
     */
    List<Transaction> findAll();

    /**
     * Retrieve transactions for a specific customer within a date range.
     *
     * @param customerId the customer ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return List of transactions matching the criteria
     */
    List<Transaction> findByCustomerIdAndDateBetween(String customerId, LocalDate startDate, LocalDate endDate);
}