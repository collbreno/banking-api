package com.example.visabreno.account;

public class AccountService {
    public AccountDTO.AccountResponse getAccount(int id) {
        if (id > 10) {
            throw new AccountNotFoundException(id);
        }
        return new AccountDTO.AccountResponse(id, "111");
    }

    public AccountDTO.AccountResponse createAccount(String document) {
        if (document.length() != 11) {
            throw new InvalidDocumentException();
        }
        return new AccountDTO.AccountResponse(1, document);
    }
}
