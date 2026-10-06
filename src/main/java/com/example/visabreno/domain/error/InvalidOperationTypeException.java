package com.example.visabreno.domain.error;

public class InvalidOperationTypeException extends RuntimeException {

    public InvalidOperationTypeException(int code) {
        super("Invalid operation code %d".formatted(code));
    }
}
