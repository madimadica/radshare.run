package com.madimadica.voidrelics.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madimadica.voidrelics.account.UserAccountService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
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

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final UserAccountService userAccountService;

    public AuthSecurityConfig(ObjectMapper objectMapper, ApplicationEventPublisher eventPublisher, UserAccountService userAccountService) {
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.userAccountService = userAccountService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomUserArgumentResolver());
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
                .requestMatchers("/api/v1/account/login").permitAll()
                .requestMatchers("/api/v1/account/logout").authenticated()
                .requestMatchers("/api/v1/mod/**").hasAnyRole(AuthRole.ADMIN.toRoleName(), AuthRole.MODERATOR.toRoleName())
                .anyRequest().permitAll()
        );
        var loginAuthFilter = new CustomLoginAuthenticationFilter(objectMapper, eventPublisher);
        var registerFilter = new AccountRegistrationFilter(objectMapper, userAccountService, loginAuthFilter);
        var logoutFilter = new CustomLogoutFilter(eventPublisher);

        http.logout(AbstractHttpConfigurer::disable);
        http.addFilterBefore(logoutFilter, RequestCacheAwareFilter.class);
        http.addFilterBefore(registerFilter, RequestCacheAwareFilter.class);
        http.addFilterBefore(loginAuthFilter, RequestCacheAwareFilter.class);

        var chain = http.build();

        loginAuthFilter.setAuthenticationManager(Objects.requireNonNull(http.getSharedObject(AuthenticationManager.class)));
        loginAuthFilter.setSessionAuthenticationStrategy(Objects.requireNonNull(http.getSharedObject(SessionAuthenticationStrategy.class)));
        loginAuthFilter.setSecurityContextRepository(Objects.requireNonNull(http.getSharedObject(SecurityContextRepository.class)));

        return chain;
    }
}
