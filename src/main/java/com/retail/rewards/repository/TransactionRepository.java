package com.retail.rewards.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.retail.rewards.model.Transaction;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class TransactionRepository {

    private List<Transaction> transactions = Collections.emptyList();

    @PostConstruct
    public void init() {
        transactions = loadTransactions(); // loads data once and store it in memory
    }

    public List<Transaction> findAll() {
        return Collections.unmodifiableList(transactions);
    }

    private List<Transaction> loadTransactions() {
        log.info("Executing TransactionRepository.loadTransactions()");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());

        try(InputStream is = TransactionRepository.class.getClassLoader().getResourceAsStream("transaction.json")) {
            if(is == null) {
                throw new IllegalStateException("Could not initialize transaction data: transaction.json not found");
            }
            List<Transaction> loadedTransactions = objectMapper.readValue(is, new TypeReference<List<Transaction>>() {});
            return loadedTransactions == null ? Collections.emptyList() : loadedTransactions;

        } catch (IOException e) {
            log.error("Failed to load the transactions:{}", String.valueOf(e));
            throw new IllegalStateException("Could not initialize transaction data" +e);
        }
    }

    public List<Transaction> findByCustomerIdAndDateAfter(@NotBlank String customerId, LocalDate cutOffdate) {
        log.info("Executing findAllByCustomerIdAndDateAfter method for customerId: {} and dateAfter: {}", customerId, cutOffdate);
        if (cutOffdate == null) {
            throw new IllegalArgumentException("Cutoff date can not be null");
        }
        return transactions.stream()
                .filter(Objects::nonNull)
                .filter(t-> Objects.equals(t.getCustomerId(),customerId))
                .filter(t-> t.getTransDate() != null)
                .filter(t-> !t.getTransDate().isBefore(cutOffdate))
                .toList();
    }
}
