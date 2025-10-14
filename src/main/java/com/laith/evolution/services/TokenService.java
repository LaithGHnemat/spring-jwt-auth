package com.laith.evolution.services;

import com.laith.evolution.dto.TokenRequestDto;
import com.laith.evolution.dto.TokenResponseDto;

public interface TokenService {
    TokenResponseDto generateToken(TokenRequestDto request);

    TokenResponseDto refreshJwtToken(String refreshTokenValue);

    void revokeRefreshToken(String refreshTokenValue);
}
