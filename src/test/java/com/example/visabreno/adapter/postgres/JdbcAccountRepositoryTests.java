package com.example.visabreno.adapter.postgres;

import com.example.visabreno.domain.Account;
import com.example.visabreno.domain.error.AccountAlreadyExistsException;
import com.example.visabreno.domain.error.AccountNotFoundException;
import com.example.visabreno.domain.error.InvalidDocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.example.visabreno.support.TestPostgres.POSTGRES;

@Tag("database-adapter")
class JdbcAccountRepositoryTests {

    private JdbcTemplate jdbcTemplate;
    private JdbcAccountRepository repository;

    @BeforeEach
    void setUp() {
        var dataSource = new DriverManagerDataSource(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword()
        );
        jdbcTemplate = new JdbcTemplate(dataSource);
        repository = new JdbcAccountRepository(jdbcTemplate);

        jdbcTemplate.execute("TRUNCATE TABLE transactions, accounts RESTART IDENTITY CASCADE");
    }

    @Nested
    class Create {

        @Test
        void createsAccountAndReturnsGeneratedId() {
            var document = "12345678900";

            var accountId = repository.create(document);

            assertThat(accountId).isPositive();
            assertThat(jdbcTemplate.queryForObject(
                    "SELECT document FROM accounts WHERE id = ?",
                    String.class,
                    accountId
            )).isEqualTo(document);
        }

        @Test
        void throwsWhenDocumentAlreadyExists() {
            var document = "12345678900";
            repository.create(document);

            assertThatThrownBy(() -> repository.create(document))
                    .isInstanceOf(AccountAlreadyExistsException.class)
                    .hasMessage("An account with this document already exists");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1234567890", "123456789012"})
        void throwsWhenDocumentHasInvalidLength(String document) {
            assertThatThrownBy(() -> repository.create(document))
                    .isInstanceOf(InvalidDocumentException.class)
                    .hasMessage("Invalid document number");
        }

        @Test
        void throwsWhenDocumentIsNull() {
            assertThatThrownBy(() -> repository.create(null))
                    .isInstanceOf(InvalidDocumentException.class)
                    .hasMessage("Invalid document number");
        }
    }

    @Nested
    class GetById {

        @Test
        void returnsAccountById() {
            var document = "12345678900";
            long accountId = Objects.requireNonNull(jdbcTemplate.queryForObject(
                    "INSERT INTO accounts (document) VALUES (?) RETURNING id",
                    Long.class,
                    document
            ));

            var account = repository.getById(accountId);

            assertThat(account).isEqualTo(new Account(accountId, document));
        }

        @Test
        void throwsWhenAccountDoesNotExist() {
            assertThatThrownBy(() -> repository.getById(999L))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessage("Account not found");
        }
    }
}
