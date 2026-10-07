package com.example.visabreno.adapter.http;

import com.example.visabreno.domain.OperationType;
import com.example.visabreno.domain.Transaction;
import com.example.visabreno.domain.TransactionService;
import com.example.visabreno.domain.error.AccountNotFoundException;
import com.example.visabreno.domain.error.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Tag("http-adapter")
class TransactionControllerTests {

    private TransactionService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(TransactionService.class);
        mockMvc = standaloneSetup(new TransactionController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Nested
    class PostTransactions {

        @Test
        void createsTransactionUsingFieldsFromJson() throws Exception {
            var amount = new BigDecimal("123.45");
            var occurredAt = OffsetDateTime.parse("2026-10-06T09:30:00-03:00");
            var transaction = new Transaction(
                    99L,
                    42L,
                    OperationType.NORMAL_PURCHASE,
                    new BigDecimal("-123.45"),
                    occurredAt
            );
            when(service.createTransaction(42L, OperationType.NORMAL_PURCHASE, amount))
                    .thenReturn(transaction);

            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "account_id": 42,
                                      "operation_type_id": 1,
                                      "amount": 123.45
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(content().json("""
                            {
                              "transaction_id": 99,
                              "account_id": 42,
                              "operation_type_id": 1,
                              "amount": -123.45,
                              "occurred_at": "2026-10-06T12:30:00Z"
                            }
                            """, JsonCompareMode.STRICT));

            verify(service).createTransaction(42L, OperationType.NORMAL_PURCHASE, amount);
        }

        @Test
        void returnsBadRequestWithoutCallingServiceWhenOperationTypeIsInvalid() throws Exception {
            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "account_id": 42,
                                      "operation_type_id": 99,
                                      "amount": 123.45
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("""
                            {
                              "title": "Bad request",
                              "detail": "Invalid operation code 99"
                            }
                            """, JsonCompareMode.LENIENT));

            verifyNoInteractions(service);
        }

        @Test
        void returnsBadRequestWhenServiceRejectsAmount() throws Exception {
            var amount = new BigDecimal("0.00");
            when(service.createTransaction(42L, OperationType.NORMAL_PURCHASE, amount))
                    .thenThrow(new InvalidAmountException("Must be positive"));

            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "account_id": 42,
                                      "operation_type_id": 1,
                                      "amount": 0.00
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("""
                            {
                              "title": "Bad request",
                              "detail": "Invalid amount: Must be positive"
                            }
                            """, JsonCompareMode.LENIENT));

            verify(service).createTransaction(42L, OperationType.NORMAL_PURCHASE, amount);
        }

        @Test
        void returnsNotFoundWhenServiceCannotFindAccount() throws Exception {
            var amount = new BigDecimal("123.45");
            when(service.createTransaction(42L, OperationType.NORMAL_PURCHASE, amount))
                    .thenThrow(new AccountNotFoundException());

            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "account_id": 42,
                                      "operation_type_id": 1,
                                      "amount": 123.45
                                    }
                                    """))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("""
                            {
                              "title": "Not found",
                              "detail": "Account not found"
                            }
                            """, JsonCompareMode.LENIENT));

            verify(service).createTransaction(42L, OperationType.NORMAL_PURCHASE, amount);
        }

        @Test
        void rejectsMalformedJsonWithoutCallingService() throws Exception {
            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"account_id\":42,\"operation_type_id\":1,\"amount\":123.45"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        void rejectsRequestWhenAmountIsOmitted() throws Exception {
            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "account_id": 42,
                                      "operation_type_id": 1
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        void rejectsRequestWhenAmountIsNull() throws Exception {
            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "account_id": 42,
                                      "operation_type_id": 1,
                                      "amount": null
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        void rejectsRequestWhenAccountIdIsOmitted() throws Exception {
            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "operation_type_id": 1,
                                      "amount": 123.45
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        void rejectsRequestWhenOperationTypeIdIsOmitted() throws Exception {
            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "account_id": 42,
                                      "amount": 123.45
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
