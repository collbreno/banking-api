package com.example.visabreno.account;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException() {
        super("An account with this document already exists");
    }
}
