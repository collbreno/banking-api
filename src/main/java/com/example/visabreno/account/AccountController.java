package com.example.visabreno.account;

import com.example.visabreno.account.AccountDTO.AccountResponse;
import com.example.visabreno.account.AccountDTO.CreateAccountRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/accounts/{id}")
    public AccountResponse getAccount(@PathVariable long id) {
        return service.getAccount(id);
    }

    @PostMapping("/accounts")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
        return service.createAccount(request.documentNumber());
    }

}
