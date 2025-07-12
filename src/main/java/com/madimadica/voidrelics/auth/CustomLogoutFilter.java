package com.madimadica.voidrelics.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

public class CustomLogoutFilter implements Filter {

    private final RequestMatcher matcher = new AntPathRequestMatcher("/api/v1/account/logout", "POST");
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    private final HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogoutFilter.class);

    private final ApplicationEventPublisher eventPublisher;

    public CustomLogoutFilter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!matcher.matches(request)) {
            chain.doFilter(request, response);
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) auth.getPrincipal();
        LOGGER.info("Logging out: {}", user.getUsername());
        csrfTokenRepository.saveToken(null, request, response);
        logoutHandler.logout(request, response, auth);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new LogoutSuccessEvent(auth));
        }
        response.setStatus(204);
    }
}
