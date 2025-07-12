package com.madimadica.voidrelics.account;

import com.madimadica.voidrelics.auth.CustomUser;
import com.madimadica.voidrelics.auth.dto.AuthenticatedUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class UserAccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountController.class);

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDto> me(CustomUser user) {
        return ResponseEntity.ok(AuthenticatedUserDto.of(user));
    }

}
