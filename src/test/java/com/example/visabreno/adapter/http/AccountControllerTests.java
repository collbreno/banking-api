package com.example.visabreno.adapter.http;

import com.example.visabreno.domain.Account;
import com.example.visabreno.domain.AccountService;
import com.example.visabreno.domain.error.AccountAlreadyExistsException;
import com.example.visabreno.domain.error.AccountNotFoundException;
import com.example.visabreno.domain.error.InvalidDocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Tag("http-adapter")
class AccountControllerTests {

    private AccountService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(AccountService.class);
        mockMvc = standaloneSetup(new AccountController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Nested
    class PostAccounts {

        @Test
        void createsAccountUsingDocumentNumberFromJson() throws Exception {
            when(service.createAccount("12345678900")).thenReturn(new Account(42L, "12345678900"));

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"document_number":"12345678900"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(content().json("""
                            {
                              "account_id": 42,
                              "document_number": "12345678900"
                            }
                            """, JsonCompareMode.STRICT));

            verify(service).createAccount("12345678900");
        }

        @Test
        void rejectsRequestWhenDocumentNumberIsOmitted() throws Exception {
            // TODO: adicionar mensagem de erro
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        void rejectsRequestWhenDocumentNumberIsNull() throws Exception {
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"document_number":null}
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        void rejectsRequestWhenDocumentNumberIsEmpty() throws Exception {
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"document_number":""}
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        void rejectsMalformedJsonWithoutCallingService() throws Exception {
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"document_number":""
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        void returnsBadRequestWhenServiceRejectsDocumentNumber() throws Exception {
            when(service.createAccount("123"))
                    .thenThrow(new InvalidDocumentException());

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"document_number":"123"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("""
                            {
                              "title": "Bad request",
                              "detail": "Invalid document number"
                            }
                            """, JsonCompareMode.LENIENT));

            verify(service).createAccount("123");
        }

        @Test
        void returnsConflictWhenServiceReportsExistingAccount() throws Exception {
            when(service.createAccount("12345678900"))
                    .thenThrow(new AccountAlreadyExistsException());

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"document_number":"12345678900"}
                                    """))
                    .andExpect(status().isConflict())
                    .andExpect(content().json("""
                            {
                              "title": "Conflict",
                              "detail": "An account with this document already exists"
                            }
                            """, JsonCompareMode.LENIENT));

            verify(service).createAccount("12345678900");
        }
    }

    @Nested
    class GetAccount {

        @Test
        void returnsAccountFromService() throws Exception {
            when(service.getAccount(42L)).thenReturn(new Account(42L, "12345678900"));

            mockMvc.perform(get("/accounts/42"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("""
                            {
                              "account_id": 42,
                              "document_number": "12345678900"
                            }
                            """, JsonCompareMode.STRICT));

            verify(service).getAccount(42L);
        }

        @Test
        void returnsNotFoundWhenServiceCannotFindAccount() throws Exception {
            when(service.getAccount(42L)).thenThrow(new AccountNotFoundException());

            mockMvc.perform(get("/accounts/42"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("""
                            {
                              "title": "Not found",
                              "detail": "Account not found"
                            }
                            """, JsonCompareMode.LENIENT));

            verify(service).getAccount(42L);
        }

        @Test
        void rejectsWhenAccountIdIsNaN() throws Exception {
            mockMvc.perform(get("/accounts/pato"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
