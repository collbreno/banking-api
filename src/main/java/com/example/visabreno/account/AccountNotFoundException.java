package com.example.visabreno.account;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(long id) {
        super("Account %d not found".formatted(id));
    }
}
