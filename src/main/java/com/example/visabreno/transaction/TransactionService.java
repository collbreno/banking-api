package com.example.visabreno.transaction;

import com.example.visabreno.account.AccountNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public TransactionDTO.PostTransactionResponse createTransaction(TransactionDTO.PostTransactionRequest request) {
        if (request.amount().signum() <= 0) {
            throw new InvalidAmountException("Must be positive");
        }

        if (request.amount().scale() != 2) {
            throw new InvalidAmountException("Must have 2 decimal units");
        }

        var operationType = OperationType.fromCode(request.operationTypeId());

        long id;
        try {
            id = repository.insert(
                    request.accountId(),
                    operationType,
                    request.amount().multiply(new BigDecimal(operationType.getSign()))
            );
        } catch (DataIntegrityViolationException exception) {
            var cause = exception.getMostSpecificCause();
            if (cause instanceof SQLException sqlException) {
                var state = sqlException.getSQLState();
                if (state.equals("23514")) {
                    throw new InvalidOperationTypeException(operationType.getCode());
                } else if (state.equals("23503")) {
                    throw new AccountNotFoundException();
                }
            }
            throw exception;
        }

        return new TransactionDTO.PostTransactionResponse(id);


    }
}
