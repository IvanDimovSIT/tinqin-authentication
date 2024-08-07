package com.tinqinacademy.authentication.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET = "temp";
    private static final long EXPIRATION_TIME = 5 * 60_000;
    private static final String ROLE_KEY = "role";

    private Key key() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String id, String role) {
        return Jwts.builder()
                .setSubject(id)
                .claim(ROLE_KEY, role)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
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
}
