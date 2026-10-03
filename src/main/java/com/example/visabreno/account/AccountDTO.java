package com.example.visabreno.account;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class AccountDTO {

    private AccountDTO() {
    }

    public record CreateAccountRequest(
            @JsonProperty("document_number") String documentNumber
    ) {
    }

    public record AccountResponse(
            @JsonProperty("account_id") long id,
            @JsonProperty("document_number") String document
    ) {
    }
}
