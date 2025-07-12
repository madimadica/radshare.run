package com.madimadica.voidrelics.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserAccountRegistrationDto(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
) {
}
