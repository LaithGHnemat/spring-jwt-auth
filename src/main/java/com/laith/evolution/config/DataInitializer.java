package com.laith.evolution.config;

import com.laith.evolution.model.Client;
import com.laith.evolution.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Configuration
@Log
public class DataInitializer {

    private final ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;


    @Bean
    public CommandLineRunner myCommandLineRunner() {
        return args -> {
            clientRepository.deleteAll();
                Client client =Client.builder()
                        .clientId("test-client")
                        .clientSecret(passwordEncoder.encode("secret123"))
                        .scope("pos")
                        .build();

                clientRepository.save(client);
                log.info("Default client inserted successfully! clientId=" + client.getClientId());
            };}
}