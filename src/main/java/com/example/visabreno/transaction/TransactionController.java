package com.example.visabreno.transaction;

import com.example.visabreno.transaction.TransactionDTO.PostTransactionRequest;
import com.example.visabreno.transaction.TransactionDTO.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse postTransaction(@RequestBody PostTransactionRequest request) {
        var transaction = service.createTransaction(
                request.accountId(),
                OperationType.fromCode(request.operationTypeId()),
                request.amount()
        );
        return new TransactionResponse(
                transaction.id(),
                transaction.accountId(),
                transaction.operation().code(),
                transaction.amount(),
                transaction.occurredAt().toInstant()
        );
    }
}
