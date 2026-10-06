package com.example.visabreno.transaction;

import java.math.BigDecimal;

public interface TransactionRepository {
    long create(long accountId, OperationType operationType, BigDecimal amount);
}
