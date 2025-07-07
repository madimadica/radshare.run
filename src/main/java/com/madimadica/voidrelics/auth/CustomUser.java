package com.madimadica.voidrelics.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  Authenticated user data. Could contain a {@code null} password depending on if this is cached.
 * </p><p>
 *  This can be injected into controllers automatically because of {@link CustomUserArgumentResolver}
 * </p>
 */
public class CustomUser implements UserDetails {
    private final long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final int rolesBitmask;
    private final List<AuthRole> roles;
    private final List<GrantedAuthority> authorities;

    public CustomUser(long id, String username, String email, String passwordHash, int rolesBitmask) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rolesBitmask = rolesBitmask;
        this.roles = List.copyOf(AuthRole.loadFromBitmask(rolesBitmask));
        this.authorities = roles.stream().map(AuthRole::toGrantedAuthority).toList();
    }

    public CustomUser(long id, String username, String email, String passwordHash, List<AuthRole> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rolesBitmask = AuthRole.writeToBitmask(roles);
        this.roles = List.copyOf(roles);
        this.authorities = roles.stream().map(AuthRole::toGrantedAuthority).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public int getRolesBitmask() {
        return rolesBitmask;
    }

    public List<AuthRole> getRoles() {
        return roles;
    }

    public boolean hasRole(AuthRole role) {
        return role.matchesBitmask(rolesBitmask);
    }

    public boolean hasElevatedRole() {
        return roles.stream().anyMatch(AuthRole::isElevated);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUser that = (CustomUser) o;
        return id == that.id && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(passwordHash, that.passwordHash) && Objects.equals(roles, that.roles) && Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, passwordHash, roles, authorities);
    }

    @Override
    public String toString() {
        return "CustomUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", rolesBitmask=" + rolesBitmask +
                ", roles=" + roles +
                '}';
    }
}
