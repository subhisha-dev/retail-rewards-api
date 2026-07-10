package com.charter.reward.service;

import com.charter.reward.exception.CustomerNotFoundException;
import com.charter.reward.model.RewardsResponse;
import com.charter.reward.model.Transaction;
import com.charter.reward.repository.TransactionRepository;
import com.charter.reward.util.RewardsCalculator;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RewardsService {

    private final TransactionRepository transactionRepository;

    public RewardsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public List<RewardsResponse> getTransactionsAllRewards() {
        List<Transaction> transactions = validateTransactions(transactionRepository.findAll());
        return transactions.stream()
                .filter(this::isRewardEligibleTransaction)
                .collect(Collectors.groupingBy(Transaction::getCustomerId))
                .entrySet().stream()
                .map(entry-> buildRewardsResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsForCustomer(String customerId) {
        List<Transaction> transactions =  validateTransactions(transactionRepository.findAll())
                .stream()
                .filter(this::isRewardEligibleTransaction)
                .filter(trans -> Objects.equals(trans.getCustomerId(), customerId))
                .toList();

        if(transactions.isEmpty()) {
            throw new CustomerNotFoundException("Customer ID:" + customerId + " not found");
        }
        return transactions;
    }

    public RewardsResponse getCustomerRewards(String customerId) {
        log.info("getCustomerRewards - customerId: {} ", customerId);
        List<Transaction> transactions = getTransactionsForCustomer(customerId);
        return buildRewardsResponse(customerId, transactions);
    }

    private RewardsResponse buildRewardsResponse(String customerId, List<Transaction> transactions) {
        log.debug("buildRewardsResponse - customerId: {} ", customerId);
        return buildCommonRewardsResponse(customerId, transactions, null, null);
    }


    public RewardsResponse getCustomerRewardsForPeriod(@NotBlank String customerId, LocalDate startDate, LocalDate endDate) {
        log.debug("Executing getCustomerRewardsForPeriod - Calculating rewards for customer:{} between {} and {}", customerId, startDate, endDate);

        List<Transaction> transactions = validateTransactions(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate)).stream()
                .filter(this::isRewardEligibleTransaction)
                .toList();
        return buildRewardsResponseForPeriod(customerId, startDate, endDate, transactions);
    }

    private RewardsResponse buildRewardsResponseForPeriod(String customerId, LocalDate startDate, LocalDate endDate, List<Transaction> transactions) {
        log.debug("Executing buildRewardsResponseForPeriod method for customer :{} for period {} - {}", customerId, startDate, endDate);
        return buildCommonRewardsResponse(customerId, transactions, startDate, endDate);
    }

    private RewardsResponse buildCommonRewardsResponse(String customerId, List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        log.debug("Executing buildCommonRewardsResponse method for customer :{} ", customerId);

        List<Transaction> validTransactions = validateTransactions(transactions).stream()
                .filter(this::isRewardEligibleTransaction)
                .toList();

        if (validTransactions.isEmpty()) {
            String errorMsg = (startDate != null && endDate != null)
                ? "No valid transactions found for CustomerID:" + customerId + " for the provided period"
                : "No valid transactions found for CustomerID:" + customerId;
            log.warn("No transactions found: {}", errorMsg);
            throw new CustomerNotFoundException(errorMsg);
        }

        // Extract customer details from first transaction with null-safety
        Transaction firstTransaction = validTransactions.getFirst();
        String customerName = firstTransaction.getCustomerName();
        if (customerName == null || customerName.isBlank()) {
            log.warn("Transaction for customer {} has null/blank name, using 'Unknown'", customerId);
            customerName = "Unknown";
        }
        int totalTransactions = validTransactions.size();

        // Calculate monthly reward points using BigDecimal
        Map<String, BigDecimal> monthlyPoints = validTransactions.stream()
                .collect(Collectors.groupingBy(
                        t -> YearMonth.from(t.getTransactionDate()).toString(),
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> RewardsCalculator.calculateRewardPoints(t.getTransactionAmount()),
                                BigDecimal::add)));

        BigDecimal totalPoints = monthlyPoints.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate periodInMonths - if dates provided, use the period; otherwise, use unique month count
        int periodInMonths;
        if (startDate != null && endDate != null) {
            long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(startDate), YearMonth.from(endDate)) + 1;
            try {
                periodInMonths = Math.toIntExact(Math.max(1, monthsBetween));
            } catch (ArithmeticException e) {
                log.warn("Date range too large, assigning period to maximum integer value");
                periodInMonths = Integer.MAX_VALUE;
            }
        } else {
            // Default to the count of unique months in transactions
            periodInMonths = !monthlyPoints.isEmpty() ? monthlyPoints.size() : 1;
        }

        // Build response with all customer details
        RewardsResponse response = new RewardsResponse();
        response.setCustomerId(customerId);
        response.setCustomerName(customerName);
        response.setPeriodInMonths(periodInMonths);
        response.setTotalTransactions(totalTransactions);
        response.setMonthlyRewardPoints(monthlyPoints);
        response.setTotalRewardPoints(totalPoints);
        response.setStartDate(startDate);
        response.setEndDate(endDate);

        return response;
    }

    private List<Transaction> validateTransactions(List<Transaction> transactions) {
        return transactions == null ? Collections.emptyList() : transactions;
    }

    private boolean isRewardEligibleTransaction(Transaction transaction) {
        return transaction != null
                && transaction.getCustomerId() != null
                && transaction.getTransactionDate() != null
                && transaction.getTransactionAmount() != null;
    }
}

