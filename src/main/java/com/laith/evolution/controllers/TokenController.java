package com.laith.evolution.controllers;

import com.laith.evolution.dto.TokenRequestDto;
import com.laith.evolution.dto.TokenResponseDto;
import com.laith.evolution.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping(value = "/token", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TokenResponseDto> generateToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType,
            @RequestParam("scope") String scope) {
        TokenRequestDto request = getTokenRequestDto(clientId, clientSecret, grantType, scope);
        TokenResponseDto tokenResponse = tokenService.generateToken(request);
        return ResponseEntity.ok(tokenResponse);
    }

    private TokenRequestDto getTokenRequestDto(String clientId, String clientSecret,
                                               String grantType, String scope) {
        return TokenRequestDto.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(grantType)
                .scope(scope)
                .build();
    }
}