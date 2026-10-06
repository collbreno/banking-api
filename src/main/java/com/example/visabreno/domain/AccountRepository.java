package com.example.visabreno.domain;

public interface AccountRepository {
    Account getById(long id);

    long create(String documentNumber);
}
