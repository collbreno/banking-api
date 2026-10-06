package com.example.visabreno.domain.error;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException() {
        super("An account with this document already exists");
    }
}
