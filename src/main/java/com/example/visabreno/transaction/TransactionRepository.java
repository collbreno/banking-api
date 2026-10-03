package com.example.visabreno.transaction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class TransactionRepository {

    private final JdbcTemplate template;

    public TransactionRepository(JdbcTemplate template) {
        this.template = template;
    }

    public long insert(long accountId, OperationType operationType, BigDecimal amount) {
        return template.queryForObject(
                "INSERT INTO transactions (operation_code, amount, account_id) VALUES (?, ?, ?) RETURNING id",
                (resultSet, rowNumber) -> resultSet.getLong("id"),
                operationType.getCode(), amount, accountId
        );
    }
}
