package io.github.skydynamic.quickbackupmulti.security;

import io.github.skydynamic.quickbackupmulti.WebUI;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class TokenUtil {
    private static final String SECRET_KEY = WebUI.getConfig().getSecretKey();

    private static SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        } catch (IllegalArgumentException e) {
            keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(String username) {
        SecretKey key = getSigningKey();
        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(key)
            .compact();
    }

    public static boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            WebUI.getLOGGER().warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }
}
