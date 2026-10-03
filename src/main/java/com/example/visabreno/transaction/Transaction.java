package com.example.visabreno.transaction;

import java.math.BigDecimal;

public record Transaction(long id, OperationType operation, int accountId, BigDecimal amount) {
}
