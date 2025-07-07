package com.madimadica.voidrelics.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserLoginRequestDto(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password
) {
}
