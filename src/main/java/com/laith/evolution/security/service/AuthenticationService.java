/*
package com.laith.evolution.security.service;

import com.laith.evolution.dto.LoginUserDto;
import com.laith.evolution.dto.RegisterUserDto;
import com.laith.evolution.model.User;
import com.laith.evolution.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // Registers a new user by encoding their password and saving them to the database.
    @Transactional
    public User signup(RegisterUserDto input) {
        User user = User.builder()
                .fullName(input.getFullName())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .build();
        return userRepository.save(user);
    }

    */
/**
     * Authenticates an existing user by checking email and password.
     * If authentication succeeds, returns the User entity.
     *//*

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));
        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }
}*/
