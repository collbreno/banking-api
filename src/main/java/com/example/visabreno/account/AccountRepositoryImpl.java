package com.example.visabreno.account;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Account getById(long id) {
        var accounts = jdbcTemplate.query(
                "SELECT id, document FROM accounts WHERE id = ?",
                (resultSet, rowNumber) -> new Account(
                        resultSet.getLong("id"),
                        resultSet.getString("document")
                ),
                id
        );

        return accounts.stream().findFirst().orElseThrow(AccountNotFoundException::new);
    }

    public long create(String document) {
        try {
            return jdbcTemplate.queryForObject(
                    "INSERT INTO accounts (document) VALUES (?) RETURNING id, document",
                    (resultSet, rowNumber) -> resultSet.getLong("id"),
                    document
            );
        } catch (DuplicateKeyException exception) {
            throw new AccountAlreadyExistsException();
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidDocumentException();
        }
    }
}
