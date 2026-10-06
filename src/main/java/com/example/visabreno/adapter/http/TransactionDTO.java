package com.example.visabreno.adapter.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public final class TransactionDTO {
    private TransactionDTO() {
    }

    public record PostTransactionRequest(
            @JsonProperty("account_id") long accountId,
            @JsonProperty("operation_type_id") int operationTypeId,
            BigDecimal amount
    ) {
    }

    public record TransactionResponse(
            @JsonProperty("transaction_id") long id,
            @JsonProperty("account_id") long accountId,
            @JsonProperty("operation_type_id") int operationTypeId,
            BigDecimal amount,
            @JsonProperty("occurred_at") Instant occurredAt
    ) {
    }
}
