package com.example.visabreno.account;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Account> findById(long id) {
        var accounts = jdbcTemplate.query(
                "SELECT id, document FROM accounts WHERE id = ?",
                (resultSet, rowNumber) -> new Account(
                        resultSet.getLong("id"),
                        resultSet.getString("document")
                ),
                id
        );

        return accounts.stream().findFirst();
    }
}
