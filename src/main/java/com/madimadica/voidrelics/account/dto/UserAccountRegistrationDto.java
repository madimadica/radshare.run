package com.madimadica.voidrelics.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserAccountRegistrationDto(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
) {
}
