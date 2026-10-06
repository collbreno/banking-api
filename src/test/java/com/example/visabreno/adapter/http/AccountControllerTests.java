package com.example.visabreno.adapter.http;

import com.example.visabreno.domain.AccountService;
import com.example.visabreno.domain.error.AccountAlreadyExistsException;
import com.example.visabreno.domain.error.InvalidDocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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

    @Test
    void createsAccountUsingDocumentNumberFromJson() throws Exception {
        when(service.createAccount("12345678900")).thenReturn(42L);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"document_number":"12345678900"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id").value(42));

        verify(service).createAccount("12345678900");
    }

    @Test
    void rejectsRequestWhenDocumentNumberIsOmitted() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
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
                .andExpect(jsonPath("$.title").value("Bad request"))
                .andExpect(jsonPath("$.detail").value("Invalid document number"));

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
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value("An account with this document already exists"));

        verify(service).createAccount("12345678900");
    }
}
