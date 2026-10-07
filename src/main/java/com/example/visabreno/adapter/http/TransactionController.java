package com.example.visabreno.adapter.http;

import com.example.visabreno.adapter.http.TransactionDTO.PostTransactionRequest;
import com.example.visabreno.adapter.http.TransactionDTO.TransactionResponse;
import com.example.visabreno.domain.OperationType;
import com.example.visabreno.domain.TransactionService;
import jakarta.validation.Valid;
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
    public TransactionResponse postTransaction(@Valid @RequestBody PostTransactionRequest request) {
        var transaction = service.createTransaction(
                request.accountId(),
                OperationType.fromCode(request.operationTypeId()),
                request.amount()
        );
        return TransactionResponse.from(transaction);
    }
}
