package com.example.visabreno.account;

public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException() {
        super("Invalid document number");
    }
}
