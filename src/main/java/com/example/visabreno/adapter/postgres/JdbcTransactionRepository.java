package com.example.visabreno.adapter.postgres;

import com.example.visabreno.domain.OperationType;
import com.example.visabreno.domain.Transaction;
import com.example.visabreno.domain.TransactionRepository;
import com.example.visabreno.domain.error.AccountNotFoundException;
import com.example.visabreno.domain.error.InvalidOperationTypeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class JdbcTransactionRepository implements TransactionRepository {

    private final JdbcTemplate template;

    public JdbcTransactionRepository(JdbcTemplate template) {
        this.template = template;
    }

    public long create(long accountId, OperationType operationType, BigDecimal amount, BigDecimal balance, OffsetDateTime dateTime) {
        try {
            return template.queryForObject(
                    "INSERT INTO transactions (operation_code, amount, balance, account_id, date_time) VALUES (?, ?, ?, ?, ?) RETURNING id",
                    (resultSet, rowNumber) -> resultSet.getLong("id"),
                    operationType.code(), amount, balance, accountId, dateTime
            );
        } catch (DataIntegrityViolationException exception) {
            var cause = exception.getMostSpecificCause();
            if (cause instanceof SQLException sqlException) {
                var state = sqlException.getSQLState();
                if (PostgresSqlState.CHECK_VIOLATION.equals(state)) {
                    throw new InvalidOperationTypeException(operationType.code());
                } else if (PostgresSqlState.FOREIGN_KEY_VIOLATION.equals(state)) {
                    throw new AccountNotFoundException();
                }
            }
            throw exception;
        }
    }

    @Override
    public List<Transaction> getNegativeTransactions(long accountId) {
        return template.query(
                "SELECT id, account_id, operation_code, amount, date_time, balance " +
                        "FROM transactions WHERE account_id = ? AND balance < 0",
                (resultSet, rowNumber) -> new Transaction(
                        resultSet.getLong("id"),
                        resultSet.getLong("account_id"),
                        OperationType.fromCode(resultSet.getInt("operation_code")),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getBigDecimal("balance"),
                        resultSet.getObject("date_time", OffsetDateTime.class)
                ),
                accountId
        );
    }

    @Override
    public void update(long transactionId, BigDecimal balance) {
        template.update("UPDATE transactions SET balance = ? WHERE id = ?", balance, transactionId);
    }


}
