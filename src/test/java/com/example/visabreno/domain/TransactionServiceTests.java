package com.example.visabreno.domain;

import com.example.visabreno.domain.error.AccountNotFoundException;
import com.example.visabreno.domain.error.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("domain")
class TransactionServiceTests {

    private static final long ACCOUNT_ID = 42L;
    private static final OffsetDateTime NOW = OffsetDateTime.parse("2026-10-06T12:30:00Z");

    private TransactionRepository repository;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionRepository.class);
        var clock = Clock.fixed(Instant.parse("2026-10-06T12:30:00Z"), ZoneOffset.UTC);
        service = new TransactionService(repository, clock);
    }

    @ParameterizedTest(name = "creates {0} with amount {1}")
    @MethodSource("operationTypes")
    void createsTransactionWithSignedAmountAndCurrentTime(
            OperationType operationType,
            BigDecimal expectedAmount
    ) {
        var inputAmount = new BigDecimal("123.45");
        when(repository.create(ACCOUNT_ID, operationType, expectedAmount, NOW)).thenReturn(99L);

        var transaction = service.createTransaction(ACCOUNT_ID, operationType, inputAmount);

        assertThat(transaction).isEqualTo(new Transaction(
                99L,
                ACCOUNT_ID,
                operationType,
                expectedAmount,
                NOW
        ));
        verify(repository).create(ACCOUNT_ID, operationType, expectedAmount, NOW);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-1.00"})
    void rejectsAmountThatIsNotPositive(String value) {
        assertThatThrownBy(() -> service.createTransaction(
                ACCOUNT_ID,
                OperationType.NORMAL_PURCHASE,
                new BigDecimal(value)
        ))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Invalid amount: Must be positive");

        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123.4", "123.450"})
    void rejectsAmountWithoutExactlyTwoDecimalUnits(String value) {
        assertThatThrownBy(() -> service.createTransaction(
                ACCOUNT_ID,
                OperationType.NORMAL_PURCHASE,
                new BigDecimal(value)
        ))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Invalid amount: Must have 2 decimal units");

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsNullAmount() {
        assertThatThrownBy(() -> service.createTransaction(
                ACCOUNT_ID,
                OperationType.NORMAL_PURCHASE,
                null
        ))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Invalid amount: Must be positive");

        verifyNoInteractions(repository);
    }

    @Test
    void propagatesRepositoryException() {
        var amount = new BigDecimal("123.45");
        var exception = new AccountNotFoundException();
        when(repository.create(
                ACCOUNT_ID,
                OperationType.CREDIT_VOUCHER,
                amount,
                NOW
        )).thenThrow(exception);

        assertThatThrownBy(() -> service.createTransaction(
                ACCOUNT_ID,
                OperationType.CREDIT_VOUCHER,
                amount
        )).isSameAs(exception);
    }

    private static Stream<Arguments> operationTypes() {
        return Stream.of(
                Arguments.of(OperationType.NORMAL_PURCHASE, new BigDecimal("-123.45")),
                Arguments.of(OperationType.PURCHASE_WITH_INSTALLMENTS, new BigDecimal("-123.45")),
                Arguments.of(OperationType.WITHDRAWAL, new BigDecimal("-123.45")),
                Arguments.of(OperationType.CREDIT_VOUCHER, new BigDecimal("123.45"))
        );
    }
}
