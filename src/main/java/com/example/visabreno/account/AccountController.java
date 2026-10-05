package com.example.visabreno.account;

import com.example.visabreno.account.AccountDTO.AccountResponse;
import com.example.visabreno.account.AccountDTO.CreateAccountRequest;
import com.example.visabreno.account.AccountDTO.CreateAccountResponse;
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
    public CreateAccountResponse createAccount(@RequestBody CreateAccountRequest request) {
        var id = service.createAccount(request.documentNumber());
        return new CreateAccountResponse(id);
    }

}
