package com.example.visabreno.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Transaction(
        long id,
        long accountId,
        OperationType operation,
        BigDecimal amount,
        OffsetDateTime occurredAt
) {
}
