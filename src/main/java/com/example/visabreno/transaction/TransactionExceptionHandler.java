package com.example.visabreno.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransactionExceptionHandler {

    @ExceptionHandler(InvalidOperationTypeException.class)
    public ProblemDetail handleInvalidOperationType(InvalidOperationTypeException exception) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad request");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ProblemDetail handleInvalidOperationType(InvalidAmountException exception) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad request");
        problem.setDetail(exception.getMessage());
        return problem;
    }
}
