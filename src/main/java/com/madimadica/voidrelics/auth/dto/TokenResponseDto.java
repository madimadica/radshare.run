package com.madimadica.voidrelics.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponseDto(
        @JsonProperty("token") String token,
        @JsonProperty("user") AuthenticatedUserResponseDto user
) {
}
