package com.example.visabreno.account;

import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDTO.AccountResponse getAccount(long id) {
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return new AccountDTO.AccountResponse(account.id(), account.document());
    }

    public AccountDTO.AccountResponse createAccount(String document) {
        if (document.length() != 11) {
            throw new InvalidDocumentException();
        }
        return new AccountDTO.AccountResponse(1, document);
    }
}
