package com.example.visabreno.account;

public interface AccountRepository {
    Account getById(long id);

    long create(String documentNumber);
}
