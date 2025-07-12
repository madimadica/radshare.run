package com.madimadica.voidrelics.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.madimadica.voidrelics.auth.dto.AuthenticatedUserDto;
import com.madimadica.voidrelics.auth.dto.UserLoginRequestDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;


/**
 * Similar to a {@link UsernamePasswordAuthenticationFilter} but with JSON API formatting.
 * Returns successes as a {@link AuthenticatedUserDto}
 */
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/v1/auth/login", "POST");
    private final ObjectMapper objectMapper;
    private SessionAuthenticationStrategy mySessionStrategy = new NullAuthenticatedSessionStrategy();

    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper, ApplicationEventPublisher eventPublisher) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
        super.setAuthenticationSuccessHandler((req, resp, auth) -> {
            resp.setStatus(200);
            resp.setContentType("application/json");
            CustomUser user = (CustomUser) auth.getPrincipal();
            objectMapper.writeValue(resp.getOutputStream(), AuthenticatedUserDto.of(user));
        });
        super.setApplicationEventPublisher(eventPublisher);
    }

    @Override
    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionStrategy) {
        super.setSessionAuthenticationStrategy(sessionStrategy);
        this.mySessionStrategy = sessionStrategy;
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            UserLoginRequestDto dto;
            try {
                String requestBody = String.join("\n", request.getReader().lines().toList());
                dto = objectMapper.readValue(requestBody, UserLoginRequestDto.class);
            } catch (JsonProcessingException jsonProcessingException) {
                throw new AuthenticationServiceException("Could not parse json payload", jsonProcessingException);
            } catch (IOException ioException) {
                throw new AuthenticationServiceException("Could not read request body", ioException);
            }
            return authenticate(request, dto.username(), dto.password());
        }
    }

    private Authentication authenticate(HttpServletRequest request, String username, String password) {
        username = username != null ? username.trim() : "";
        password = password != null ? password.trim() : "";
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public Authentication loginHelper(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        Authentication auth = null;
        try {
            try {
                auth = authenticate(request, username, password);
                mySessionStrategy.onAuthentication(auth, request, response);
                this.successfulAuthentication(request, response, null, auth);
            }  catch (InternalAuthenticationServiceException e) {
                this.logger.error("An internal error occurred while trying to authenticate the user.", e);
                this.unsuccessfulAuthentication(request, response, e);
            } catch (AuthenticationException e) {
                this.unsuccessfulAuthentication(request, response, e);
            }
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
        return auth;
    }
}
