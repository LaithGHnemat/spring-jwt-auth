package com.laith.evolution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDto {
    @JsonProperty("error")
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
}