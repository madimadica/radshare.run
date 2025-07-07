package com.madimadica.voidrelics.auth.jwt;

import com.madimadica.voidrelics.auth.CustomUser;

/**
 * Represents the user details stored in their JWT
 */
record JwtUser(
        long id,
        String username,
        String email,
        int rolesBitmask
) {
    public CustomUser toUserDetails() {
        return new CustomUser(
                id,
                username,
                email,
                "",
                rolesBitmask
        );
    }
}
