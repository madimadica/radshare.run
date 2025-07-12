package com.madimadica.voidrelics.account;

import com.madimadica.voidrelics.account.dto.UserAccountRegistrationDto;
import com.madimadica.voidrelics.auth.AuthSecurityConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class UserAccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountController.class);

    private final UserAccountService userAccountService;
    private final AuthSecurityConfig authSecurityConfig;

    public UserAccountController(UserAccountService userAccountService, AuthSecurityConfig authSecurityConfig) {
        this.userAccountService = userAccountService;
        this.authSecurityConfig = authSecurityConfig;
    }

    @PostMapping("/register")
    public void register(@RequestBody UserAccountRegistrationDto dto, HttpServletRequest request, HttpServletResponse response) {
        var userDetails = userAccountService.register(dto).toCustomUser();
        LOGGER.info("Registered new user {}", userDetails);
        // Automatically log in the newly registered user
        var auth = authSecurityConfig.jsonUsernamePasswordAuthenticationFilter().loginHelper(request, response, dto.username(), dto.password());
        LOGGER.info("Logged in new user {}", auth.getPrincipal());
    }
}
