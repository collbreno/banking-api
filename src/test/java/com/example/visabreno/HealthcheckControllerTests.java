package com.example.visabreno;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthcheckControllerTests {

    private final HealthcheckController controller = new HealthcheckController();

    @Test
    void reportsThatTheApplicationIsUp() {
        assertThat(controller.healthcheck().status()).isEqualTo("UP");
    }
}
