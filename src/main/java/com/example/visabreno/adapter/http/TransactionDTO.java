package com.example.visabreno.adapter.http;

import com.example.visabreno.domain.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public final class TransactionDTO {
    private TransactionDTO() {
    }

    public record PostTransactionRequest(
            @NotNull @JsonProperty("account_id") Long accountId,
            @NotNull @JsonProperty("operation_type_id") Integer operationTypeId,
            @NotNull BigDecimal amount
    ) {
    }

    public record TransactionResponse(
            @JsonProperty("transaction_id") long id,
            @JsonProperty("account_id") long accountId,
            @JsonProperty("operation_type_id") int operationTypeId,
            BigDecimal amount,
            @JsonProperty("occurred_at") Instant occurredAt
    ) {
        public static TransactionResponse from(Transaction transaction) {
            return new TransactionResponse(
                    transaction.id(),
                    transaction.accountId(),
                    transaction.operation().code(),
                    transaction.amount(),
                    transaction.occurredAt().toInstant()
            );
        }
    }
}
