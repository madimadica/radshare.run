package com.madimadica.voidrelics.account;

import com.madimadica.voidrelics.account.dto.UserAccountRegistrationDto;
import com.madimadica.voidrelics.auth.AuthController;
import com.madimadica.voidrelics.auth.dto.AuthenticatedUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class UserAccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountController.class);

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticatedUserDto> register(@RequestBody UserAccountRegistrationDto dto, HttpServletRequest request, HttpServletResponse response) {
        var userDetails = userAccountService.register(dto).toCustomUser();
        LOGGER.info("Registered new user {}", userDetails);

        // Automatically log in the newly registered user
        var authToken = UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        AuthController.createAuthContext(authToken, request, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(AuthenticatedUserDto.of(userDetails));
    }
}
