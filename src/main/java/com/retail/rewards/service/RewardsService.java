package com.retail.rewards.service;

import com.retail.rewards.dto.RewardsResponse;
import com.retail.rewards.exception.CustomerNotFoundException;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.util.RewardsCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RewardsService {

    private final TransactionRepository transactionRepository;

    private RewardsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public List<RewardsResponse> getTransactionsAllRewards() {
        return transactionRepository.findAll().stream().collect(Collectors.groupingBy(Transaction::getCustomerId))
                .entrySet().stream().map(entry-> buildRewardsResponse(entry.getKey(), entry.getValue())).collect(Collectors.toList());

    }

    public List<Transaction> getTransactionsForCustomer(String customerId) {
        List<Transaction> transactions =  transactionRepository.findAll().stream().
                filter(trans -> trans.getCustomerId().equals(customerId)).toList();
        if(transactions.isEmpty()) {
            throw new CustomerNotFoundException("Customer ID:" + customerId + " not found");
        }
        return transactions;
    }

    public RewardsResponse getCustomerRewards(String customerId) {
        List<Transaction> transactions = getTransactionsForCustomer(customerId);
        return buildRewardsResponse(customerId, transactions);
    }

    private RewardsResponse buildRewardsResponse(String customerId, List<Transaction> transactions) {

        Map<String, Double> monthlyPoints = transactions.stream().collect(Collectors.groupingBy(t-> YearMonth.from(t.getTransDate()).toString(), Collectors.summingDouble(t-> RewardsCalculator.calculateRewardPoints(t.getTransAmount()))));

        double totalPoints = monthlyPoints.values().stream().mapToDouble(Double:: doubleValue).sum();
        return new RewardsResponse(customerId, monthlyPoints, totalPoints);
    }

    public RewardsResponse getCustomerRewardsForDate(String customerId, LocalDate date) {
        YearMonth targetMonth = YearMonth.from(date);

        List<Transaction> transactions = getTransactionsForCustomer(customerId);
        if(transactions.isEmpty()) {
            throw new CustomerNotFoundException("No transactions found for CustomerID:" + customerId + " for target month:" + targetMonth.toString());
        }
        return buildRewardsResponse(customerId, transactions.stream().filter(t-> YearMonth.from(t.getTransDate()).equals(targetMonth)).toList());

    }
}
