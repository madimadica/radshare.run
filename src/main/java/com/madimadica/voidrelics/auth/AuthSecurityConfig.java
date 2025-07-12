package com.madimadica.voidrelics.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Objects;

/**
 * Configured the auth manager, password encoder, and web filters
 * (disables CSRF, restricts elevated endpoints, sets stateless session, adds JWT auth filter)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthSecurityConfig implements WebMvcConfigurer {

    private JsonUsernamePasswordAuthenticationFilter loginAuthFilter = null;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public AuthSecurityConfig(ObjectMapper objectMapper, ApplicationEventPublisher eventPublisher) {
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        return loginAuthFilter;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomUserArgumentResolver());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    /**
     * Return the default auth manager which implicitly uses the registered UserDetailsService and PasswordEncoder beans
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Default: Enable CSRF, Enable CORS, implicit JSESSIONID cookie associated with UserDetails

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/auth/logout").authenticated()
                .requestMatchers("/api/v1/mod/**").hasAnyRole(AuthRole.ADMIN.toRoleName(), AuthRole.MODERATOR.toRoleName())
                .anyRequest().permitAll()
        );
        loginAuthFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper, eventPublisher);
        http.addFilterAfter(loginAuthFilter, LogoutFilter.class);

        var chain = http.build();

        loginAuthFilter.setAuthenticationManager(Objects.requireNonNull(http.getSharedObject(AuthenticationManager.class)));
        loginAuthFilter.setSessionAuthenticationStrategy(Objects.requireNonNull(http.getSharedObject(SessionAuthenticationStrategy.class)));
        loginAuthFilter.setSecurityContextRepository(Objects.requireNonNull(http.getSharedObject(SecurityContextRepository.class)));

        return chain;
    }
}
