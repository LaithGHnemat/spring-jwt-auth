package com.laith.evolution.controllers;

import com.laith.evolution.annotations.BlacklistCheck;
import com.laith.evolution.annotations.RateLimit;
import com.laith.evolution.services.SensitiveInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aop-secured")
@RequiredArgsConstructor
public class AopSecuredController {

    private final SensitiveInfoService sensitiveInfoService;

    @GetMapping("/sensitive-info")
    public ResponseEntity<String> getSensitiveInfo() {
        return ResponseEntity.ok(sensitiveInfoService.getSensitiveInfo());
    }

    @GetMapping("/financial-data")
    @RateLimit(maxAttempts = 3, durationInSeconds = 120)
    @BlacklistCheck
    public ResponseEntity<String> getFinancialData() {
        return ResponseEntity.ok("Financial data protected via AOP and rate limiting.");
    }

    @GetMapping("/user-stats")
    @RateLimit(maxAttempts = 10, durationInSeconds = 30)
    @BlacklistCheck
    public ResponseEntity<String> getUserStats() {
        return ResponseEntity.ok("User statistics monitored and rate-limited via AOP.");
    }
}
