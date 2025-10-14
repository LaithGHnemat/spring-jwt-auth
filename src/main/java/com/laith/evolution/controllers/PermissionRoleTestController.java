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
public class PermissionRoleTestController {

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

    @GetMapping("/client/write")
    @PreAuthorize("hasAuthority('USER_READ')") // CLIENT role can access here only
    public ResponseEntity<String> testClientWrite() {
        return ResponseEntity.ok("client can write!");
    }

    @GetMapping("/user/read")
    @PreAuthorize("hasAuthority('CLIENT_READ')") // USER role can access here only
    public ResponseEntity<String> testUserRead() {
        return ResponseEntity.ok("user can read!");
    }
}