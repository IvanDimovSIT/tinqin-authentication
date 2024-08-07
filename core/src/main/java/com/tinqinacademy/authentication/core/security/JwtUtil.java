package com.tinqinacademy.authentication.core.security;

import com.tinqinacademy.authentication.persistence.model.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    //TODO: Move secret key outside of class
    private static final String SECRET = "7e7c8376f2892c07c5607202a48eb16700db7ea11853d4b2446faebdd5e2094f26f528096162dacb21b1a1059ff56518d8952d01f68d30011c538dc2b47afe6cdcfc5019eb51f8dfbf321c279c8c95dff72b33966c44ca4e1d811e76561227ed0f34ec4e0a90d39eb20cc9366cfdd99dad8070c40ac2fa8761c32aa5c59c77bbfc34b61734dd4faa9d592312b86d1f3ddfcd2baba655a501ab1911446f30e8d0ad39e3e2d366a079253372e5fee0766b91408e362b7fb5ed78b07a105157573570789c156061b66aa7537538f26dbd886cdb71ca8a7828704ef36e87ef5846d13c503b9b6df9b01f0df446a7c3639847b882798bcfe288fb7658b05f2914306c";
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
