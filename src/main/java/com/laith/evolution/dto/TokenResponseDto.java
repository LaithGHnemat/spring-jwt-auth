package com.laith.evolution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseDto {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";
    @JsonProperty("expires_in")
    private long expiresIn;
    @JsonProperty("scope")
    private String scope;
}
