package io.github.skydynamic.quickbackupmulti.security;

import io.github.skydynamic.quickbackupmulti.WebUI;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;

public class TokenUtil {
    private static final String SECRET_KEY = WebUI.getConfig().getSecretKey();
    private static final long TOKEN_TTL_MS = 86_400_000L;

    private static SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        } catch (IllegalArgumentException e) {
            keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) {
            try {
                MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                keyBytes = sha256.digest(keyBytes);
            } catch (Exception ex) {
                WebUI.getLOGGER().error("Failed to derive signing key: {}", ex.getMessage());
                throw new RuntimeException(ex);
            }
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken() {
        try {
            SecretKey key = getSigningKey();
            return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TOKEN_TTL_MS))
                .signWith(key)
                .compact();
        } catch (Exception e) {
            WebUI.getLOGGER().error("Failed to generate token: {}", e.getMessage());
            return null;
        }
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
