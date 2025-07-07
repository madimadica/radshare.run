package com.madimadica.voidrelics.auth;

import com.madimadica.voidrelics.auth.dto.AuthenticatedUserResponseDto;
import com.madimadica.voidrelics.auth.dto.TokenResponseDto;
import com.madimadica.voidrelics.auth.dto.UserLoginRequestDto;
import com.madimadica.voidrelics.auth.jwt.JwtAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtAuthService jwtAuthService;

    public AuthController(AuthenticationManager authenticationManager, JwtAuthService jwtAuthService) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthService = jwtAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody UserLoginRequestDto loginDto) {
        LOGGER.info("Attempting to login as {}", loginDto.username());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );
        CustomUser userDetails = (CustomUser) authentication.getPrincipal();
        var tokenDto = jwtAuthService.issueNewToken(userDetails);
        return ResponseEntity.ok(tokenDto);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponseDto> me(CustomUser user) {
        return ResponseEntity.ok(AuthenticatedUserResponseDto.of(user));
    }

}
