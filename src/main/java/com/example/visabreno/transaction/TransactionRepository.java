package com.example.visabreno.transaction;

import java.math.BigDecimal;

public interface TransactionRepository {
    public long create(long accountId, OperationType operationType, BigDecimal amount);
}
