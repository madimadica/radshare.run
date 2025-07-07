package com.madimadica.voidrelics.account;

import com.madimadica.voidrelics.auth.AuthRole;
import com.madimadica.voidrelics.auth.CustomUser;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;


/**
 * Represents a row from the database table {@code user_account} for a single user account
 * @param id unique user ID for VoidRelics
 * @param username Warframe username/IGN
 * @param email Optional/nullable email tied to this user's account
 * @param passwordHash the user's hashed/encrypted password
 * @param rolesBitmask the raw bitmask of denormalized roles for fast reads
 */
public record UserAccount(
        long id,
        String username,
        String email,
        String passwordHash,
        int rolesBitmask
) {
    public static final RowMapper<UserAccount> MAPPER = (rs, i) -> new UserAccount(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getInt("roles_bitmask")
    );

    public List<AuthRole> unwrapRoles() {
        return AuthRole.loadFromBitmask(rolesBitmask);
    }

    public CustomUser toCustomUser() {
        return new CustomUser(
                id,
                username,
                email,
                passwordHash,
                unwrapRoles()
        );
    }
}
