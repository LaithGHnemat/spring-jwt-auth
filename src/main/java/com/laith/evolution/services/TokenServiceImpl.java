package com.laith.evolution.services;

import com.laith.evolution.dto.TokenRequestDto;
import com.laith.evolution.dto.TokenResponseDto;
import com.laith.evolution.exceptions.InvalidClientCredentialsException;
import com.laith.evolution.exceptions.InvalidRefreshTokenException;
import com.laith.evolution.exceptions.InvalidScopeException;
import com.laith.evolution.exceptions.UnsupportedGrantTypeException;
import com.laith.evolution.model.Client;
import com.laith.evolution.model.RefreshToken;
import com.laith.evolution.repositories.ClientRepository;
import com.laith.evolution.repositories.RefreshTokenRepository;
import com.laith.evolution.security.model.ClientDetailsImpl;
import com.laith.evolution.security.service.JwtUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final JwtUtility jwtService;
    private final ClientRepository clientRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${oauth.grant_type.client_credentials}")
    private String clientCredentialsGrantType;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
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

        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = getRefreshToken(refreshTokenValue, client);
        refreshTokenRepository.save(refreshToken);

        log.info("Generated JWT for client: {}", client.getClientId());
        log.info("Generated Refresh Token for client: {}, token: {}", client.getClientId(), refreshTokenValue);

        return TokenResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .scope(client.getScope())
                .build();
    }
    @Override
    public TokenResponseDto refreshJwtToken(String refreshTokenValue) {
        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (oldRefreshToken.isRevoked() || oldRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
        }

        oldRefreshToken.setRevoked(true);
        oldRefreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(oldRefreshToken);

        Client client = oldRefreshToken.getClient();
        UserDetails clientUserDetails = new ClientDetailsImpl(client);
        String jwtToken = jwtService.generateToken(clientUserDetails);

        String newRefreshTokenValue = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = getRefreshToken(newRefreshTokenValue, client);
        refreshTokenRepository.save(newRefreshToken);

        log.info("Refreshed JWT for client: {}", client.getClientId());
        log.info("Old Refresh Token revoked: {}", refreshTokenValue);
        log.info("New Refresh Token issued: {}", newRefreshTokenValue);

        return TokenResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(newRefreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .scope(client.getScope())
                .build();
    }
    @Override
    public void revokeRefreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        log.info("Refresh Token revoked manually: {} for client: {}", refreshTokenValue, refreshToken.getClient().getClientId());
    }

    private RefreshToken getRefreshToken(String refreshTokenValue, Client client) {
        return RefreshToken.builder()
                .token(refreshTokenValue)
                .client(client)
                .createdAt(Instant.now())
                .expiryDate(Instant.now().plusSeconds(refreshTokenExpiration))
                .revoked(false)
                .build();
    }

}