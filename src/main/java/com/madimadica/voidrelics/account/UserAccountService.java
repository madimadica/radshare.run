package com.madimadica.voidrelics.account;

import com.madimadica.voidrelics.account.dto.UserAccountRegistrationDto;
import com.madimadica.voidrelics.auth.AuthRole;
import com.madimadica.voidrelics.exceptions.ApiError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserAccountService {

    private final UserAccountDao userAccountDao;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountDao userAccountDao, PasswordEncoder passwordEncoder) {
        this.userAccountDao = userAccountDao;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAccount register(UserAccountRegistrationDto dto) {
        dto = validate(dto);
        var insert = new UserAccountDao.Insert(
                dto.username(),
                dto.email(),
                encodePassword(dto),
                List.of(AuthRole.USER)
        );
        return userAccountDao.insert(insert);
    }

    public String encodePassword(UserAccountRegistrationDto dto) {
        return passwordEncoder.encode(dto.password());
    }

    /**
     * Validate and return the normalized values
     * @throws ApiError if any part is invalid
     */
    public UserAccountRegistrationDto validate(UserAccountRegistrationDto dto) throws ApiError {
        String username = validateUsername(dto.username());
        String email = validateEmail(dto.email());
        String password = validatePassword(dto.password());
        validateUsernameAndEmailAvailability(username, email);
        return new UserAccountRegistrationDto(username, email, password);
    }

    public void validateUsernameAvailability(String username) {
        var $user = userAccountDao.findByUsername(username);
        if ($user.isPresent()) {
            throw new ApiError(409, "Username already taken");
        }
    }

    /**
     * Optimized single query validation
     * @param username username to validate availability for
     * @param email nullable email to validate availability for
     */
    public void validateUsernameAndEmailAvailability(String username, String email) {
        var users = userAccountDao.findAllByUsernameOrEmail(email, username);
        if (users.isEmpty()) {
            return;
        }
        if (email == null) {
            throw new ApiError(409, "Username already taken");
        }
        boolean foundUsername = false;
        boolean foundEmail = false;
        for (var user : users) {
            if (username.equalsIgnoreCase(user.username())) {
                foundUsername = true;
            }
            if (email.equalsIgnoreCase(user.email())) {
                foundEmail = true;
            }
        }
        if (foundUsername && foundEmail) {
            throw new ApiError(409, "Username and email already taken");
        } else {
            throw new ApiError(409, "Email already taken");
        }
    }

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._,-]{4,24}$");

    /**
     * Trims and validates usernames according to <a href="https://support.warframe.com/hc/en-us/articles/200182160-Can-I-change-my-alias">WARFRAME Support</a>
     * <br>
     * Your alias must be at least 4 characters long.  The only special characters that are allowed in your username are . , - and _ .   Your alias must meet the community guidelines outlined here:  Player Name Policy
     *
     * @param username Username to validate
     * @return the validated, trimmed username, if it is valid.
     * @throws ApiError if invalid
     */
    public String validateUsername(String username) {
        if (username == null) {
            throw new ApiError(400, "Username cannot be null");
        }
        username = username.trim();
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ApiError(404, "Invalid username");
        }
        return username;
    }

    private static final Pattern HAS_LOWER = Pattern.compile("[a-z]");
    private static final Pattern HAS_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern HAS_DIGIT = Pattern.compile("\\d");
    private static final Pattern HAS_SYMBOL = Pattern.compile("[^a-zA-Z0-9]");

    public String validatePassword(String password) {
        if (password == null) {
            throw new ApiError(400, "Password cannot be null");
        }
        password = password.trim();
        final int minLength = 12;
        if (password.length() < minLength) {
            throw new ApiError(400, "Password must be at least " + minLength + " characters");
        }
        final int maxLength = 72;
        if (password.length() > maxLength) {
            throw new ApiError(400, "Password cannot exceed " + maxLength + " characters");
        }

        if (HAS_SYMBOL.matcher(password).find()) {
            return password; // At least one symbol is enough
        }

        // Otherwise require at least 2 of uppercase/lowercase/digit
        boolean hasLower = HAS_LOWER.matcher(password).find();
        boolean hasUpper = HAS_UPPER.matcher(password).find();
        boolean hasDigit = HAS_DIGIT.matcher(password).find();
        int total = (hasLower ? 1 : 0) + (hasUpper ? 1 : 0) + (hasDigit ? 1 : 0);
        if (total < 2) {
            throw new ApiError(400, "Password must contain at least one symbol, or at least 2 of [Uppercase, Lowercase, Digit]");
        }
        return password;
    }

    // Almost RFC compliant
    private static final Pattern EMAIL_REGEX = Pattern.compile("^([\\p{L}0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*)$");

    public String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        email = email.trim();
        if (email.length() > 500) {
            throw new ApiError(400, "Invalid email length");
        }
        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new ApiError(400, "Invalid email");
        }
        return email;
    }
}
