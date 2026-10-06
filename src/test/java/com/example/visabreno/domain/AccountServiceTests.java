package com.example.visabreno.domain;

import com.example.visabreno.domain.error.AccountNotFoundException;
import com.example.visabreno.domain.error.InvalidDocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AccountServiceTests {

    private AccountRepository repository;
    private AccountService service;

    @BeforeEach
    void setUp() {
        repository = mock(AccountRepository.class);
        service = new AccountService(repository);
    }

    @Test
    void createsAccountWithIdGeneratedByRepository() {
        when(repository.create("12345678900")).thenReturn(42L);

        var account = service.createAccount("12345678900");

        assertThat(account).isEqualTo(new Account(42L, "12345678900"));
        verify(repository).create("12345678900");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890", "123456789012"})
    void rejectsDocumentWithInvalidLength(String document) {
        assertThatThrownBy(() -> service.createAccount(document))
                .isInstanceOf(InvalidDocumentException.class)
                .hasMessage("Invalid document number");

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsNullDocument() {
        assertThatThrownBy(() -> service.createAccount(null))
                .isInstanceOf(InvalidDocumentException.class)
                .hasMessage("Invalid document number");

        verifyNoInteractions(repository);
    }

    @Test
    void returnsAccountFromRepository() {
        var expectedAccount = new Account(42L, "12345678900");
        when(repository.getById(42L)).thenReturn(expectedAccount);

        var account = service.getAccount(42L);

        assertThat(account).isEqualTo(expectedAccount);
        verify(repository).getById(42L);
    }

    @Test
    void propagatesRepositoryException() {
        var exception = new AccountNotFoundException();
        when(repository.getById(42L)).thenThrow(exception);

        assertThatThrownBy(() -> service.getAccount(42L))
                .isSameAs(exception);
    }
}
