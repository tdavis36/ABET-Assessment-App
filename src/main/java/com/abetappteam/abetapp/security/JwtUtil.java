package com.abetappteam.abetapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // TODO: Update to use env variable
    @Value("""
            ${jwt.secret:VPEEoRJaj3TYWq1oNGWUeE9tEp6d4e7mAVhVNjVj39trxvGl0Pjyab2e56dMNW8k
            GYyqCxxKQ+C54Pp+v4riR6Otl2GZLVYrPnnnQlmO9dA2Ewy1KGK3ajKN5Bsb0m1B
            Q6uSNV0yqDZu23gBrYHnAbataD6W/I18irlrd8o+WO56cQAa8JNXfMq+5Y/VCRFP
            WSSEIxXc+TD8IjJUs+a3Mkr8Nsmp09mMouuldUYFOt5L7tWvzCmi3wN3xL70Neok
            AHazdvQ5Qd+T6cn5u4Z413InMFt8xA0I0S00pwFVPKfuqaHTsCm0yTe4VjqZRWkv
            /UW3CBSROeZ7agO2mayqJw==}""")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long userId, String email, String role, Long programId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("programId", programId); // Include program context

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Long extractProgramId(String token) {
        return extractClaims(token).get("programId", Long.class);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}