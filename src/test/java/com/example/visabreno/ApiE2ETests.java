package com.example.visabreno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.example.visabreno.support.TestPostgres.POSTGRES;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiE2ETests {

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE transactions, accounts RESTART IDENTITY CASCADE");
    }

    @Test
    void completesHappyPath() throws Exception {
        var createAccountResponse = post("/accounts", """
                {"document_number":"12345678900"}
                """);

        assertThat(createAccountResponse.statusCode()).isEqualTo(201);
        var createdAccount = json(createAccountResponse);
        var accountId = createdAccount.get("account_id").asLong();
        assertThat(accountId).isPositive();
        assertThat(createdAccount.get("document_number").asString()).isEqualTo("12345678900");

        var getAccountResponse = get("/accounts/" + accountId);

        assertThat(getAccountResponse.statusCode()).isEqualTo(200);
        var retrievedAccount = json(getAccountResponse);
        assertThat(retrievedAccount.get("account_id").asLong()).isEqualTo(accountId);
        assertThat(retrievedAccount.get("document_number").asString()).isEqualTo("12345678900");

        var createTransactionResponse = post("/transactions", """
                {
                  "account_id": %d,
                  "operation_type_id": 1,
                  "amount": 100.00
                }
                """.formatted(accountId));

        assertThat(createTransactionResponse.statusCode()).isEqualTo(201);
        var createdTransaction = json(createTransactionResponse);
        assertThat(createdTransaction.get("transaction_id").asLong()).isPositive();
        assertThat(createdTransaction.get("account_id").asLong()).isEqualTo(accountId);
        assertThat(createdTransaction.get("operation_type_id").asInt()).isEqualTo(1);
        assertThat(createdTransaction.get("amount").decimalValue()).isEqualByComparingTo("-100.00");
        assertThat(createdTransaction.get("occurred_at").asString()).isNotBlank();
    }

    @Test
    void returnsExpectedErrors() throws Exception {
        var accountBody = """
                {"document_number":"12345678900"}
                """;
        assertThat(post("/accounts", accountBody).statusCode()).isEqualTo(201);

        var duplicateAccountResponse = post("/accounts", accountBody);
        assertProblem(
                duplicateAccountResponse,
                409,
                "Conflict",
                "An account with this document already exists"
        );

        var missingAccountResponse = get("/accounts/999");
        assertProblem(missingAccountResponse, 404, "Not found", "Account not found");

        var missingTransactionAccountResponse = post("/transactions", """
                {
                  "account_id": 999,
                  "operation_type_id": 4,
                  "amount": 100.00
                }
                """);
        assertProblem(missingTransactionAccountResponse, 404, "Not found", "Account not found");
    }

    private HttpResponse<String> get(String path) throws Exception {
        var request = HttpRequest.newBuilder(uri(path)).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        var request = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private JsonNode json(HttpResponse<String> response) throws Exception {
        return objectMapper.readTree(response.body());
    }

    private void assertProblem(
            HttpResponse<String> response,
            int status,
            String title,
            String detail
    ) throws Exception {
        assertThat(response.statusCode()).isEqualTo(status);
        var problem = json(response);
        assertThat(problem.get("title").asString()).isEqualTo(title);
        assertThat(problem.get("detail").asString()).isEqualTo(detail);
    }
}
