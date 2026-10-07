package com.example.visabreno.adapter.postgres;

import com.example.visabreno.domain.OperationType;
import com.example.visabreno.domain.error.AccountNotFoundException;
import com.example.visabreno.domain.error.InvalidOperationTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.example.visabreno.support.TestPostgres.POSTGRES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("database-adapter")
class JdbcTransactionRepositoryTests {

    private JdbcTemplate jdbcTemplate;
    private JdbcTransactionRepository repository;

    @BeforeEach
    void setUp() {
        var dataSource = new DriverManagerDataSource(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword()
        );
        jdbcTemplate = new JdbcTemplate(dataSource);
        repository = new JdbcTransactionRepository(jdbcTemplate);

        jdbcTemplate.execute("TRUNCATE TABLE transactions, accounts RESTART IDENTITY CASCADE");
    }

    @Nested
    class Create {

        @Test
        void createsTransactionAndReturnsGeneratedId() {
            var accountId = createAccount();

            var transactionId = repository.create(
                    accountId,
                    OperationType.NORMAL_PURCHASE,
                    new BigDecimal("-123.45"),
                    OffsetDateTime.parse("2026-10-06T12:34:56-03:00")
            );

            assertThat(transactionId).isPositive();
            assertThat(jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM transactions WHERE id = ?",
                    Long.class,
                    transactionId
            )).isEqualTo(1L);
        }

        @Test
        void persistsTransactionDataWithoutLoss() {
            var accountId = createAccount();
            var amount = new BigDecimal("-123.45");
            var dateTime = OffsetDateTime.parse("2026-10-06T12:34:56-03:00");

            var transactionId = repository.create(
                    accountId,
                    OperationType.NORMAL_PURCHASE,
                    amount,
                    dateTime
            );

            var persisted = jdbcTemplate.queryForObject(
                    "SELECT operation_code, amount, account_id, date_time FROM transactions WHERE id = ?",
                    (resultSet, rowNumber) -> new PersistedTransaction(
                            resultSet.getInt("operation_code"),
                            resultSet.getBigDecimal("amount"),
                            resultSet.getLong("account_id"),
                            resultSet.getObject("date_time", OffsetDateTime.class)
                    ),
                    transactionId
            );

            assertThat(persisted).isNotNull();
            assertThat(persisted.operationCode()).isEqualTo(OperationType.NORMAL_PURCHASE.code());
            assertThat(persisted.amount()).isEqualByComparingTo(amount);
            assertThat(persisted.accountId()).isEqualTo(accountId);
            assertThat(persisted.dateTime().toInstant()).isEqualTo(dateTime.toInstant());
        }

        @Test
        void throwsWhenAccountDoesNotExist() {
            assertThatThrownBy(() -> repository.create(
                    999L,
                    OperationType.CREDIT_VOUCHER,
                    new BigDecimal("100.00"),
                    OffsetDateTime.parse("2026-10-06T12:34:56-03:00")
            ))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessage("Account not found");
        }

        @Test
        void throwsWhenOperationCodeViolatesDatabaseConstraint() {
            var accountId = createAccount();
            var invalidOperationType = mock(OperationType.class);
            when(invalidOperationType.code()).thenReturn(99);

            assertThatThrownBy(() -> repository.create(
                    accountId,
                    invalidOperationType,
                    new BigDecimal("100.00"),
                    OffsetDateTime.parse("2026-10-06T12:34:56-03:00")
            ))
                    .isInstanceOf(InvalidOperationTypeException.class)
                    .hasMessage("Invalid operation code 99");
        }
    }

    private long createAccount() {
        return Objects.requireNonNull(jdbcTemplate.queryForObject(
                "INSERT INTO accounts (document) VALUES (?) RETURNING id",
                Long.class,
                "12345678900"
        ));
    }

    private record PersistedTransaction(
            int operationCode,
            BigDecimal amount,
            long accountId,
            OffsetDateTime dateTime
    ) {
    }
}
