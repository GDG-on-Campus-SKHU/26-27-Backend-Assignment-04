
package com.group.security.global.jwt;

import com.group.security.domain.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                       @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Access Token 생성: 사용자 역할도 함께 저장
    public String createAccessToken(Long userId, UserRole role) {
        return createToken(userId, ACCESS, accessTokenExpiration, role);
    }

    // Refresh Token 생성: 기존 방식 유지
    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH, refreshTokenExpiration);
    }

    private String createToken(Long userId, String type, long expiration) {
        return createToken(userId, type, expiration, null);
    }

    private String createToken(Long userId, String type, long expiration, UserRole role) {
        Date now = new Date();

        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", type)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration));

        // 역할 정보가 있을 때만 토큰에 저장
        if (role != null) {
            builder.claim("role", role.name());
        }

        return builder
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims getClaimsFromAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!ACCESS.equals(claims.get("type", String.class))) {
            throw new JwtException("Access Token이 아닙니다.");
        }

        String role = claims.get("role", String.class);

        if (role == null) {
            throw new JwtException("Access Token에 역할 정보가 없습니다.");
        }

        return claims;
    }

    public Long getUserIdFromRefreshToken(String token) {
        return getUserId(token, REFRESH);
    }

    private Long getUserId(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!expectedType.equals(claims.get("type", String.class))) {
            throw new JwtException("토큰 종류가 올바르지 않습니다.");
        }

        return Long.valueOf(claims.getSubject());
    }
}
