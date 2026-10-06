package com.example.visabreno.adapter.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthcheckController {

    @GetMapping("/healthcheck")
    public HealthcheckResponse healthcheck() {
        return new HealthcheckResponse("UP");
    }

    public record HealthcheckResponse(String status) {
    }
}
