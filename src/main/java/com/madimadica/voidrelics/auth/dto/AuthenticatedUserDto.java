package com.madimadica.voidrelics.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.madimadica.voidrelics.auth.AuthRole;
import com.madimadica.voidrelics.auth.CustomUser;

import java.util.List;

/**
 * A DTO containing the authenticated user data. This should never be given to the public. Hence, package private
 */
public record AuthenticatedUserDto(
        @JsonProperty("id") long accountId,
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("roles") List<String> roles
) {
    public static AuthenticatedUserDto of(CustomUser user) {
        return new AuthenticatedUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(AuthRole::toRoleName).toList()
        );
    }
}
