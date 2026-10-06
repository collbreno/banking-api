package com.example.visabreno.transaction;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public long createTransaction(long accountId, OperationType operationType, BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new InvalidAmountException("Must be positive");
        }

        if (amount.scale() != 2) {
            throw new InvalidAmountException("Must have 2 decimal units");
        }

        return repository.create(
                accountId,
                operationType,
                amount.multiply(new BigDecimal(operationType.getSign()))
        );
    }
}
