package com.madimadica.voidrelics.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.madimadica.voidrelics.auth.CustomUser;
import com.madimadica.voidrelics.auth.dto.AuthenticatedUserResponseDto;
import com.madimadica.voidrelics.auth.dto.TokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class JwtAuthService {
    private static final String CLAIM_KEY_USERNAME = "u";
    private static final String CLAIM_KEY_EMAIL = "e";
    private static final String CLAIM_KEY_ROLESBITMASK = "r";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtAuthService(@Value("${misc.jwt-secret}") String jwtSecret) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.verifier = JWT.require(this.algorithm).build();
    }

    public TokenResponseDto issueNewToken(CustomUser user) {
        String token = this.generateToken(user);
        var userDto = AuthenticatedUserResponseDto.of(user);
        return new TokenResponseDto(token, userDto);
    }

    String generateToken(CustomUser userDetails) {
        var now = Instant.now();
        return JWT.create()
                .withSubject(String.valueOf(userDetails.getId()))
                .withIssuedAt(now)
                .withExpiresAt(now.plus(15, ChronoUnit.DAYS))
                .withClaim(CLAIM_KEY_USERNAME, userDetails.getUsername())
                .withClaim(CLAIM_KEY_EMAIL, userDetails.getEmail())
                .withClaim(CLAIM_KEY_ROLESBITMASK, userDetails.getRolesBitmask())
                .sign(this.algorithm);
    }

    Optional<JwtUser> validateToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            var userJwt = new JwtUser(
                    Long.parseLong(jwt.getSubject()),
                    jwt.getClaim(CLAIM_KEY_USERNAME).asString(),
                    jwt.getClaim(CLAIM_KEY_EMAIL).asString(),
                    jwt.getClaim(CLAIM_KEY_ROLESBITMASK).asInt()
            );
            return Optional.of(userJwt);
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}
