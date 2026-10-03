package com.example.visabreno.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class TransactionDTO {
    private TransactionDTO() {
    }

    public record PostTransactionRequest(
            @JsonProperty("account_id") long accountId,
            @JsonProperty("operation_type_id") int operationTypeId,
            BigDecimal amount
    ) {
    }

    public record PostTransactionResponse(
            @JsonProperty("transaction_id") long id
    ) {
    }
}
