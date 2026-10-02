package com.example.visabreno.account;

public class AccountService {
    public AccountController.AccountResponse getAccount(int id) {
        if (id > 10) {
            throw new AccountNotFoundException(id);
        }
        return new AccountController.AccountResponse(id, "111");
    }

    public AccountController.AccountResponse createAccount(String document) {
        if (document.length() != 11) {
            throw new InvalidDocumentException();
        }
        return new AccountController.AccountResponse(1, document);
    }
}