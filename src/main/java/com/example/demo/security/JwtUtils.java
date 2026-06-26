package com.example.demo.security;

import com.example.demo.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final SecretKey signingKey;
    private final SecurityProperties securityProperties;

    public JwtUtils(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(securityProperties.getJwt().getSecret()));
    }

    public String generateToken(UserDetails userDetails) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(securityProperties.getJwt().getAccessTokenTtl());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Instant extractExpiration(String token) {
        return extractClaims(token).getExpiration().toInstant();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = extractClaims(token);
        return userDetails.getUsername().equals(claims.getSubject())
                && claims.getExpiration().toInstant().isAfter(Instant.now());
    }

    public List<String> extractAuthorities(String token) {
        return extractClaims(token).get("authorities", List.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
