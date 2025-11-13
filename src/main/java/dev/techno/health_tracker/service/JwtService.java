package dev.techno.health_tracker.service;

import dev.techno.health_tracker.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public final class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder().header().add("alg", "HS256")
                .and().subject(userDetails.getUsername())
                .issuer("me")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
                .claim("roles", userDetails.getAuthorities())
                .signWith(obtainSecretKey()).compact();
    }

    public <T> T readClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(readAllClaims(token));
    }

    private Claims readAllClaims(String token) {
        return Jwts.parser().verifyWith(obtainSecretKey()).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obtainSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

}
