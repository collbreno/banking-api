package com.example.visabreno.domain.error;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super("Invalid amount: %s".formatted(message));
    }
}
