package com.example.visabreno.configuration;

import com.example.visabreno.domain.AccountRepository;
import com.example.visabreno.domain.AccountService;
import com.example.visabreno.domain.TransactionRepository;
import com.example.visabreno.domain.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ApplicationConfiguration {

    @Bean
    AccountService accountService(AccountRepository repository) {
        return new AccountService(repository);
    }

    @Bean
    TransactionService transactionService(TransactionRepository repository, Clock clock) {
        return new TransactionService(repository, clock);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
