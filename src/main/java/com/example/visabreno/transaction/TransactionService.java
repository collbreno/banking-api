package com.example.visabreno.transaction;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction createTransaction(long accountId, OperationType operationType, BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new InvalidAmountException("Must be positive");
        }

        if (amount.scale() != 2) {
            throw new InvalidAmountException("Must have 2 decimal units");
        }

        var signedAmount = amount.multiply(new BigDecimal(operationType.sign()));
        var now = OffsetDateTime.now();

        var createdId = repository.create(
                accountId,
                operationType,
                signedAmount,
                now
        );

        return new Transaction(createdId, accountId, operationType, signedAmount, now);
    }
}
