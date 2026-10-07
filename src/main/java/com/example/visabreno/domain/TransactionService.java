package com.example.visabreno.domain;

import com.example.visabreno.domain.error.InvalidAmountException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;

public class TransactionService {

    private final TransactionRepository repository;
    private final Clock clock;

    public TransactionService(TransactionRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Transaction createTransaction(long accountId, OperationType operationType, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("Must be positive");
        }

        if (amount.scale() != 2) {
            throw new InvalidAmountException("Must have 2 decimal units");
        }

        var signedAmount = amount.multiply(new BigDecimal(operationType.sign()));
        var now = OffsetDateTime.now(clock);

        var createdId = repository.create(
                accountId,
                operationType,
                signedAmount,
                now
        );

        return new Transaction(createdId, accountId, operationType, signedAmount, now);
    }
}
