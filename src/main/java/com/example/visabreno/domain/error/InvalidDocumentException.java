package com.example.visabreno.domain.error;

public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException() {
        super("Invalid document number");
    }
}
