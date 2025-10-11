package com.laith.evolution.config;

import com.laith.evolution.model.Client;
import com.laith.evolution.model.Role;
import com.laith.evolution.repositories.ClientRepository;
import com.laith.evolution.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Configuration
@Log
public class DataInitializer {

    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner myCommandLineRunner() {
        return args -> {
            insertData();
        };
    }

    private void insertData() {
        clientRepository.deleteAll();
        roleRepository.deleteAll();

        Role clientRole = Role.builder().name("CLIENT").build();
        Role userRole = Role.builder().name("USER").build();
        roleRepository.save(clientRole);
        roleRepository.save(userRole);

        Client client = Client.builder()
                .clientId("test-client")
                .clientSecret(passwordEncoder.encode("secret123"))
                .scope("pos")
                .roles(Set.of(clientRole))
                .build();
        clientRepository.save(client);

        Client userClient = Client.builder()
                .clientId("test-user")
                .clientSecret(passwordEncoder.encode("user123"))
                .scope("pos")
                .roles(Set.of(userRole))
                .build();
        clientRepository.save(userClient);
    }
}