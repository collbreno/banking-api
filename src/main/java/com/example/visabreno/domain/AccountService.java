package com.example.visabreno.domain;

import com.example.visabreno.domain.error.InvalidDocumentException;

public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account getAccount(long id) {
        return repository.getById(id);
    }

    public Account createAccount(String document) {
        if (document.length() != 11) {
            throw new InvalidDocumentException();
        }
        var createdId = repository.create(document);
        return new Account(createdId, document);
    }
}
