package com.laith.evolution.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    @GetMapping("/client/protected")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> getProtectedClientData() {
        return ResponseEntity.ok("Hello, CLIENT! You can access your protected data.");
    }

    @GetMapping("/user/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getUserInfo() {
        return ResponseEntity.ok("Hello, USER! Only USER can access this endpoint.");
    }
}