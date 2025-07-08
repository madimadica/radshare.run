package com.madimadica.voidrelics.controllers;

import com.madimadica.voidrelics.auth.CustomUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.util.Optional;

@ControllerAdvice
public class ModelAttributesControllerAdvice {

    @ModelAttribute("APP_USER")
    public CustomUser bindUser(Optional<CustomUser> user) {
        return user.orElse(null);
    }

    @ModelAttribute("CURRENT_YEAR")
    public int addYear() {
        return LocalDateTime.now().getYear();
    }

}
