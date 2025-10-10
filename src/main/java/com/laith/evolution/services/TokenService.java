package com.laith.evolution.services;

import com.laith.evolution.dto.TokenRequestDto;
import com.laith.evolution.dto.TokenResponseDto;
import com.laith.evolution.exceptions.InvalidClientCredentialsException;
import com.laith.evolution.exceptions.InvalidScopeException;
import com.laith.evolution.exceptions.InvalidTokenException;
import com.laith.evolution.exceptions.UnsupportedGrantTypeException;
import com.laith.evolution.model.Client;
import com.laith.evolution.repositories.ClientRepository;
import com.laith.evolution.security.model.ClientDetailsImpl;
import com.laith.evolution.security.service.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtility jwtService;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${oauth.grant_type.client_credentials}")
    private String clientCredentialsGrantType;

    public TokenResponseDto generateToken(TokenRequestDto request) {

        Client client = clientRepository.findByClientId(request.getClientId())
                .orElseThrow(() -> new InvalidClientCredentialsException("Invalid client Id"));


        if (!passwordEncoder.matches(request.getClientSecret(), client.getClientSecret())) {
            throw new InvalidClientCredentialsException("Invalid client credentials");
        }

        if (!clientCredentialsGrantType.equals(request.getGrantType())) {
            throw new UnsupportedGrantTypeException("Grant type not supported");
        }

        if (!request.getScope().equals(client.getScope())) {
            throw new InvalidScopeException("Requested scope not allowed for this client");
        }

        UserDetails clientUserDetails = new ClientDetailsImpl(client);
        String jwtToken = jwtService.generateToken(clientUserDetails);

        return TokenResponseDto.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .scope(request.getScope())
                .build();
    }

}