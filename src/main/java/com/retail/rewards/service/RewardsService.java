package com.retail.rewards.service;

import com.retail.rewards.dto.RewardsResponse;
import com.retail.rewards.exception.CustomerNotFoundException;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.util.RewardsCalculator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
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
        log.info("buildRewardsResponse - customerId: {} ", customerId);

        List<Transaction> validTransactions = validateTransactions(transactions).stream()
                .filter(this::isRewardEligibleTransaction)
                .toList();

        Map<String, Double> monthlyPoints = validTransactions.stream()
                .collect(Collectors.groupingBy(
                        t-> YearMonth.from(t.getTransDate()).toString(),
                        Collectors.summingDouble(t-> RewardsCalculator.calculateRewardPoints(t.getTransAmount()))));

        double totalPoints = monthlyPoints.values().stream().mapToDouble(Double:: doubleValue).sum();
        return new RewardsResponse(customerId, monthlyPoints, totalPoints);
    }

    public RewardsResponse getCustomerRewardsForDate(String customerId, LocalDate date) {
        log.info("getCustomerRewardsForDate - customerId: {} ", customerId);

        if (date == null) {
            throw new IllegalArgumentException("Date can not be null");
        }

        YearMonth targetMonth = YearMonth.from(date);

        List<Transaction> transactions = getTransactionsForCustomer(customerId).stream()
                .filter(t -> YearMonth.from(t.getTransDate()).equals(targetMonth))
                .toList();
        if(transactions.isEmpty()) {
            throw new CustomerNotFoundException("No transactions found for CustomerID:" + customerId + " for target month:" + targetMonth.toString());
        }
        return buildRewardsResponse(customerId, transactions);

    }

    public RewardsResponse getCustomerRewardsForMonths(@NotBlank String customerId, @Min(1) @Max(12) int months) {
        log.info("Executing getCustomerRewardsForMonths - Calculating rewards for customer:{} for last {} months", customerId, months);
        LocalDate cutOffDate = LocalDate.now().minusMonths(months);
        log.info("cutOffDate {}", cutOffDate);

        List<Transaction> transactions = validateTransactions(transactionRepository.findByCustomerIdAndDateAfter(customerId, cutOffDate)).stream()
                .filter(this::isRewardEligibleTransaction)
                .toList();

        if(transactions.isEmpty()) {
            throw new CustomerNotFoundException("No transactions found for CustomerID:" + customerId + " for last month:" + months);
        }
        return buildRewardsResponseForMonths(customerId, months, transactions);

    }

    private RewardsResponse buildRewardsResponseForMonths(String customerId, int months, List<Transaction> transactions) {
        log.info("Executing buildRewardsResponseForMonths method for customer :{} for last {} months", customerId, months);

        List<Transaction> validTransactions = validateTransactions(transactions).stream()
                .filter(this::isRewardEligibleTransaction)
                .toList();
        if (validTransactions.isEmpty()) {
            throw new CustomerNotFoundException("No valid transactions found for CustomerID:" + customerId + " for last month:" + months);
        }
        Map<String, Double> monthlyPoints = validTransactions.stream()
                .collect(Collectors.groupingBy(
                        t-> YearMonth.from(t.getTransDate()).toString(),
                        Collectors.summingDouble(t-> RewardsCalculator.calculateRewardPoints(t.getTransAmount()))));

        String customerName = validTransactions.getFirst().getCustomerName();
        int transactionCount = validTransactions.size();
        double totalPoints = monthlyPoints.values().stream().mapToDouble(Double:: doubleValue).sum();
        return new RewardsResponse(customerId, customerName, months, transactionCount, monthlyPoints, totalPoints);
    }

    private List<Transaction> validateTransactions(List<Transaction> transactions) {
        return transactions == null ? Collections.emptyList() : transactions;
    }

    private boolean isRewardEligibleTransaction(Transaction transaction) {
        return transaction != null
                && transaction.getCustomerId() != null
                && transaction.getTransDate() != null
                && transaction.getTransAmount() != null;
    }
}
