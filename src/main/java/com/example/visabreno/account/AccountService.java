package com.example.visabreno.account;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public AccountDTO.AccountResponse getAccount(long id) {
        var account = repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return new AccountDTO.AccountResponse(account.id(), account.document());
    }

    public AccountDTO.AccountResponse createAccount(String document) {
        Account account;

        try {
            account = repository.insert(document);
        } catch (DuplicateKeyException exception) {
            throw new AccountAlreadyExistsException();
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidDocumentException();
        }

        return new AccountDTO.AccountResponse(account.id(), account.document());
    }
}
