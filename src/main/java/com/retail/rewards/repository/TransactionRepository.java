package com.retail.rewards.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.retail.rewards.model.Transaction;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class TransactionRepository {

    List<Transaction> transactions;

    @PostConstruct
    public void init() {
        transactions = loadTransactions(); // loads data once and store it in memory
    }

    public List<Transaction> findAll() {
        return Collections.unmodifiableList(transactions);
    }

    private List<Transaction> loadTransactions() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule()); // tells jackson how to parse the string value in date field to local date // by default jackson does know about string, int, double

        try(InputStream is = TransactionRepository.class.getClassLoader().getResourceAsStream("transaction.json")) {
        return objectMapper.readValue(is, new TypeReference<List<Transaction>>(){}); //due to java's type erasure feature, we use this typereference : jackson hack to preserve the full generic type List<Transaction> at runtime

        } catch (IOException e) {
            log.error("Failed to load the transactions:{}", String.valueOf(e));
            throw new IllegalStateException("Could not initialize transaction data" +e);
        }
    }

}
