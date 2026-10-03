package com.example.visabreno.transaction;

import com.example.visabreno.transaction.TransactionDTO.PostTransactionRequest;
import com.example.visabreno.transaction.TransactionDTO.PostTransactionResponse;
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
    public PostTransactionResponse postTransaction(@RequestBody PostTransactionRequest request) {
        return service.createTransaction(request);
    }
}
