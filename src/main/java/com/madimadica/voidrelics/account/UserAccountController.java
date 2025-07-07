package com.madimadica.voidrelics.account;

import com.madimadica.voidrelics.account.dto.UserAccountRegistrationDto;
import com.madimadica.voidrelics.auth.dto.TokenResponseDto;
import com.madimadica.voidrelics.auth.jwt.JwtAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class UserAccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountController.class);

    private final UserAccountService userAccountService;
    private final JwtAuthService jwtAuthService;

    public UserAccountController(UserAccountService userAccountService, JwtAuthService jwtAuthService) {
        this.userAccountService = userAccountService;
        this.jwtAuthService = jwtAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@RequestBody UserAccountRegistrationDto dto) {
        var newUser = userAccountService.register(dto).toCustomUser();
        LOGGER.info("Registered new user {}", newUser);
        var tokenDto = jwtAuthService.issueNewToken(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenDto);
    }
}
