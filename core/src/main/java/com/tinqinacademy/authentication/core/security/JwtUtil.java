package com.tinqinacademy.authentication.core.security;

import com.tinqinacademy.authentication.api.exception.exceptions.InvalidAuthenticationHeaderException;
import com.tinqinacademy.authentication.persistence.model.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET;
    private static final long EXPIRATION_TIME = 5 * 60_000;
    private static final String ROLE_KEY = "role";

    private Key key() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(UUID id, UserRole role) {
        return Jwts.builder()
                .setSubject(id.toString())
                .claim(ROLE_KEY, role.toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            return !Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get(ROLE_KEY, String.class)
                    .equals(UserRole.UNKNOWN.toString());
        } catch (Exception e) {
            return false;
        }
    }

    public UserToken extract(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.getSubject();
        String role = claims.get(ROLE_KEY, String.class);

        return new UserToken(userId, role);
    }

    public String getTokenFromHeader(String header) {
        if(header == null || !header.startsWith("Bearer ")){
            throw new InvalidAuthenticationHeaderException();
        }
        return header.substring(7);
    }
}
