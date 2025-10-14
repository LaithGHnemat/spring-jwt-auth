package com.laith.evolution.config;

import com.laith.evolution.enums.PermissionName;
import com.laith.evolution.enums.RoleName;
import com.laith.evolution.model.Client;
import com.laith.evolution.model.Permission;
import com.laith.evolution.model.Role;
import com.laith.evolution.repositories.ClientRepository;
import com.laith.evolution.repositories.PermissionRepository;
import com.laith.evolution.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Log
@Component
@Configuration
@RequiredArgsConstructor
public class SecurityDataLoader {

    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;


    @Bean
    public CommandLineRunner loadInitialSecurityData() {
        return args -> {
            setupDefaultAuthData();
        };
    }

    private void setupDefaultAuthData() {
        clientRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();

        Permission clientRead = Permission.builder()
                .name(PermissionName.CLIENT_READ)
                .build();

        Permission clientWrite = Permission.builder()
                .name(PermissionName.USER_READ)
                .build();

        permissionRepository.saveAll(List.of(clientRead, clientWrite));

        Role clientRole = Role.builder()
                .name(RoleName.CLIENT)
                .permissions(Set.of(clientWrite))
                .build();

        Role userRole = Role.builder()
                .name(RoleName.USER)
                .permissions(Set.of(clientRead))
                .build();

        roleRepository.saveAll(List.of(clientRole, userRole));

        Client client = Client.builder() // Use those data with postman to validate everything
                .clientId("test-client")
                .clientSecret(passwordEncoder.encode("secret123"))
                .scope("pos")
                .roles(Set.of(clientRole))
                .build();


        Client userClient = Client.builder()
                .clientId("test-user")
                .clientSecret(passwordEncoder.encode("user123"))
                .scope("pos")
                .roles(Set.of(userRole))
                .build();

        clientRepository.saveAll(List.of(client, userClient));
    }
}