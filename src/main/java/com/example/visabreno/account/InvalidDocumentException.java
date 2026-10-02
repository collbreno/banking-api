package com.example.visabreno.account;

public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException() {
        super("Document must contain exactly 11 characters");
    }
}
