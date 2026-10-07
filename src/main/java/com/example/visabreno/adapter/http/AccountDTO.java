package com.example.visabreno.adapter.http;

import com.example.visabreno.domain.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public final class AccountDTO {

    private AccountDTO() {
    }

    public record CreateAccountRequest(
            @NotNull
            @NotEmpty
            @JsonProperty("document_number") String documentNumber
    ) {
    }

    public record AccountResponse(
            @JsonProperty("account_id") long id,
            @JsonProperty("document_number") String document
    ) {
        public static AccountResponse from(Account account) {
            return new AccountResponse(account.id(), account.document());
        }
    }

}
