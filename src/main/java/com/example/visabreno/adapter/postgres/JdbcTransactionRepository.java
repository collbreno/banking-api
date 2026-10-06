package com.example.visabreno.adapter.postgres;

import com.example.visabreno.domain.OperationType;
import com.example.visabreno.domain.TransactionRepository;
import com.example.visabreno.domain.error.AccountNotFoundException;
import com.example.visabreno.domain.error.InvalidOperationTypeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Repository
public class JdbcTransactionRepository implements TransactionRepository {

    private final JdbcTemplate template;

    public JdbcTransactionRepository(JdbcTemplate template) {
        this.template = template;
    }

    public long create(long accountId, OperationType operationType, BigDecimal amount, OffsetDateTime dateTime) {
        try {
            return template.queryForObject(
                    "INSERT INTO transactions (operation_code, amount, account_id, date_time) VALUES (?, ?, ?, ?) RETURNING id",
                    (resultSet, rowNumber) -> resultSet.getLong("id"),
                    operationType.code(), amount, accountId, dateTime
            );
        } catch (DataIntegrityViolationException exception) {
            var cause = exception.getMostSpecificCause();
            if (cause instanceof SQLException sqlException) {
                var state = sqlException.getSQLState();
                if (state.equals("23514")) {
                    throw new InvalidOperationTypeException(operationType.code());
                } else if (state.equals("23503")) {
                    throw new AccountNotFoundException();
                }
            }
            throw exception;
        }
    }
}
