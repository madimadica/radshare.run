package com.madimadica.voidrelics.auth;

import com.madimadica.voidrelics.auth.dto.AuthenticatedUserDto;
import com.madimadica.voidrelics.auth.dto.UserLoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticatedUserDto> login(@RequestBody UserLoginRequestDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("Attempting to login as {}", loginDto.username());
        var authRequest = new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        createAuthContext(authentication, request, response);

        CustomUser user = (CustomUser) authentication.getPrincipal();
        return ResponseEntity.ok(AuthenticatedUserDto.of(user));
    }

    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        LOGGER.info("Logging out: {}", user.getUsername());
        logoutHandler.logout(request, response, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDto> me(CustomUser user) {
        return ResponseEntity.ok(AuthenticatedUserDto.of(user));
    }

    private static final SecurityContextRepository SECURITY_CONTEXT_REPOSITORY = new HttpSessionSecurityContextRepository();

    public static void createAuthContext(Authentication auth, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // This saves the context into the session the "official" way
        SECURITY_CONTEXT_REPOSITORY.saveContext(context, request, response);
    }
}
