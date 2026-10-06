package com.example.visabreno.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface TransactionRepository {
    long create(long accountId, OperationType operationType, BigDecimal amount, OffsetDateTime dateTime);
}
