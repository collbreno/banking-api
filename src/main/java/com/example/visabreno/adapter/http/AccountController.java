package com.example.visabreno.adapter.http;

import com.example.visabreno.adapter.http.AccountDTO.AccountResponse;
import com.example.visabreno.adapter.http.AccountDTO.CreateAccountRequest;
import com.example.visabreno.domain.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/accounts/{id}")
    public AccountResponse getAccount(@PathVariable long id) {
        var account = service.getAccount(id);
        return new AccountResponse(account.id(), account.document());
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        var account = service.createAccount(request.documentNumber());
        return new AccountResponse(account.id(), account.document());
    }

}
