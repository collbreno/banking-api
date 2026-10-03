package com.example.visabreno.transaction;

public class InvalidOperationTypeException extends RuntimeException {

    public InvalidOperationTypeException(int code) {
        super("Invalid operation code %d".formatted(code));
    }
}
