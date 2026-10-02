package com.example.visabreno;

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
