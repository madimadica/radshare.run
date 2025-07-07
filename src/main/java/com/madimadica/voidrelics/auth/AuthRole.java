package com.madimadica.voidrelics.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * Authorization roles for users. Can be used at method level with @PreAuthorize such as
 * {@code @PreAuthorize(AuthRole.IS_USER)}
 */
public enum AuthRole {
    USER(1),
    MODERATOR(1 << 1),
    ADMIN(1 << 2);

    public static final String IS_USER = "hasRole('USER')";
    public static final String IS_MODERATOR = "hasRole('MODERATOR')";
    public static final String IS_ADMIN = "hasRole('ADMIN')";

    private static final AuthRole[] ROLES = AuthRole.values();
    private final int bitmask;

    AuthRole(int bitmask) {
        this.bitmask = bitmask;
    }

    public int getBitmask() {
        return bitmask;
    }

    public GrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority(this.toAuthorityName());
    }

    public String toAuthorityName() {
        return "ROLE_" + this.toRoleName();
    }

    public String toRoleName() {
        return this.name();
    }

    public boolean matchesBitmask(int rolesBitmask) {
        return (rolesBitmask & this.bitmask) != 0;
    }

    public boolean isElevated() {
        return this != USER;
    }

    public static List<AuthRole> loadFromBitmask(int rolesBitmask) {
        List<AuthRole> roles = new ArrayList<>();
        for (AuthRole role : ROLES) {
            if (role.matchesBitmask(rolesBitmask)) {
                roles.add(role);
            }
        }
        return roles;
    }

    public static int writeToBitmask(List<AuthRole> roles) {
        int bitmask = 0;
        for (AuthRole role : roles) {
            bitmask |= role.bitmask;
        }
        return bitmask;
    }
}
