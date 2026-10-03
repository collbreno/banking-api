package com.example.visabreno.transaction;

import com.example.visabreno.account.AccountNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
            throw new AccountNotFoundException();
        }

        return new TransactionDTO.PostTransactionResponse(id);


    }
}
