package dev.techno.health_tracker.service.impl;

import dev.techno.health_tracker.config.properties.JwtProperties;
import dev.techno.health_tracker.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public final class JwtServiceImpl implements JwtService {

    private static final String ISSUER = "me";
    private static final String ALGORITHM = "HS256";
    private static final String ROLES_CLAIM = "roles";

    private final JwtProperties jwtProperties;
    private SecretKey cachedSecretKey;

    @Override
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.expiration());

        return Jwts.builder()
                .header()
                .add("alg", ALGORITHM).and()
                .subject(userDetails.getUsername())
                .issuer(ISSUER)
                .issuedAt(now)
                .expiration(expiration)
                .claim(ROLES_CLAIM, userDetails.getAuthorities())
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return readClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            String issuer = extractIssuer(token);
            return ISSUER.equals(issuer) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private <T> T readClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = readAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return readClaim(token, Claims::getExpiration);
    }

    private String extractIssuer(String token) {
        return readClaim(token, Claims::getIssuer);
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            log.debug("Token expiration check failed: {}", e.getMessage());
            return true;
        }
    }

    private Claims readAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        if (cachedSecretKey == null) {
            cachedSecretKey = Keys.hmacShaKeyFor(
                    jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
            );
        }
        return cachedSecretKey;
    }

}