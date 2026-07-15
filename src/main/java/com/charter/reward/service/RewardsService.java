package com.charter.reward.service;

import com.charter.reward.exception.CustomerNotFoundException;
import com.charter.reward.model.MonthlyReward;
import com.charter.reward.model.RewardsResponse;
import com.charter.reward.model.Transaction;
import com.charter.reward.model.TransactionDetail;
import com.charter.reward.repository.TransactionRepository;
import com.charter.reward.util.RewardsCalculator;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

        //calculate reward points for each transaction
        List<TransactionDetail> transactionDetails = validTransactions.stream()
                .map(tx -> {
                    BigDecimal points = RewardsCalculator.calculateRewardPoints(tx.getTransactionAmount());
                    return new TransactionDetail(
                            tx.getTransactionId(),
                            tx.getTransactionDate(),
                            tx.getTransactionAmount(),
                            points,
                            tx.getTransactionDate().getYear(),
                            tx.getTransactionDate().getMonth().toString()
                    );
                })
                .toList();

        // Group transactions by year and month for monthly reward calculation
        Map<String, List<TransactionDetail>> groupedByMonth = transactionDetails.stream()
                .collect(Collectors.groupingBy(td -> td.getYear() + "-" + td.getMonth()));

        List<MonthlyReward> monthlyRewards = groupedByMonth.values().stream()
                .map(list -> {
                    TransactionDetail first = list.getFirst();
                    BigDecimal monthTotal = list.stream()
                            .map(TransactionDetail::getRewardPoints)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new MonthlyReward(first.getYear(), first.getMonth(), monthTotal);
                })
                .sorted(Comparator.comparing(MonthlyReward::getYear)
                        .thenComparing(MonthlyReward::getMonth))
                .toList();

        BigDecimal totalPoints = transactionDetails.stream()
                .map(TransactionDetail::getRewardPoints)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate periodInMonths
        int periodInMonths;
        if (startDate != null && endDate != null) {
            long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(startDate), YearMonth.from(endDate)) + 1;
            periodInMonths = (int) Math.max(1, monthsBetween);
        } else {
            periodInMonths = !monthlyRewards.isEmpty() ? monthlyRewards.size() : 1;
        }

        RewardsResponse response = new RewardsResponse();
        response.setCustomerId(customerId);
        response.setCustomerName(customerName);
        response.setPeriodInMonths(periodInMonths);
        response.setTotalTransactions(totalTransactions);
        response.setTransactions(transactionDetails);
        response.setMonthlyRewardPoints(monthlyRewards);
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

