package com.example.visabreno.transaction;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super("Invalid amount: %s".formatted(message));
    }
}
