package com.example.visabreno.account;

import com.example.visabreno.account.AccountDTO.AccountResponse;
import com.example.visabreno.account.AccountDTO.CreateAccountRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    @GetMapping("/accounts/{id}")
    public AccountResponse getAccount(@PathVariable int id) {
        var s = new AccountService();
        return s.getAccount(id);
    }

    @PostMapping("/accounts")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
        var s = new AccountService();
        return s.createAccount(request.documentNumber());
    }

}
