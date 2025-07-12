package com.madimadica.voidrelics.auth;

import com.madimadica.voidrelics.auth.dto.AuthenticatedUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final ApplicationEventPublisher eventPublisher;

    public AuthController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    // Login through JsonUsernamePasswordAuthenticationFilter

    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        LOGGER.info("Logging out: {}", user.getUsername());
        csrfTokenRepository.saveToken(null, request, response);
        logoutHandler.logout(request, response, authentication);
        eventPublisher.publishEvent(new LogoutSuccessEvent(authentication));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDto> me(CustomUser user) {
        return ResponseEntity.ok(AuthenticatedUserDto.of(user));
    }

}
