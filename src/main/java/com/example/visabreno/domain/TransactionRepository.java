package com.example.visabreno.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionRepository {
    long create(long accountId, OperationType operationType, BigDecimal amount, BigDecimal balance, OffsetDateTime dateTime);

    List<Transaction> getNegativeTransactions(long accountId);

    void update(long transactionId, BigDecimal balance);
}
