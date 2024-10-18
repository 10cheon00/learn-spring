package com.learn.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.CompressionAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.learn.security.SecurityConstants.*;

@Component
public class JwtGenerator {
    public boolean validateToken(String token) {
        SecretKey key = getSecretKey();
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Jwt was expired or incorrect.");
        }
    }

    public String getUsernameFromJWT(String token) {
        SecretKey key = getSecretKey();
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        SecretKey key = getSecretKey();
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(expireDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
        return token;
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    }
}
