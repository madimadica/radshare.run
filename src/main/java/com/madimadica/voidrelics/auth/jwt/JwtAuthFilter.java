package com.madimadica.voidrelics.auth.jwt;

import com.madimadica.voidrelics.auth.CustomUser;
import com.madimadica.voidrelics.auth.CustomUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthService jwtAuthService;
    private final CustomUserService customUserService;

    public JwtAuthFilter(JwtAuthService jwtAuthService, CustomUserService customUserService) {
        this.jwtAuthService = jwtAuthService;
        this.customUserService = customUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Optional<String> $token = getBearerToken(request);
        if ($token.isPresent()) {
            Optional<JwtUser> $jwt = jwtAuthService.validateToken($token.get());
            if ($jwt.isPresent()) {
                JwtUser jwt = $jwt.get();
                CustomUser userDetails = jwt.toUserDetails();
                if (userDetails.hasElevatedRole()) {
                    // Always force refresh elevated users to ensure their access has not been revoked
                    userDetails = customUserService.loadUserByUsername(userDetails.getUsername());
                }
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // setting principal, used in CustomUserArgumentResolver
                        null,
                        userDetails.getAuthorities()
                );
                var requestDetails = new WebAuthenticationDetailsSource().buildDetails(request);
                authToken.setDetails(requestDetails);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }

    static Optional<String> getBearerToken(HttpServletRequest request) {
        return getBearerToken(request.getHeader("Authorization"));
    }

    static Optional<String> getBearerToken(String headerAuth) {
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return Optional.of(headerAuth.substring(7));
        }
        return Optional.empty();
    }
}
