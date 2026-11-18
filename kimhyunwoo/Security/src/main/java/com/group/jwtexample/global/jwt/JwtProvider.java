package com.group.jwtexample.global.jwt;

import com.group.jwtexample.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
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
    private static final String TYPE_CLAIM = "type";
    private static final String ROLE_CLAIM = "role";

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

    public record AccessTokenPayload(Long userId, Role role) {
    }

    public String createAccessToken(Long userId, Role role) {
        return tokenBuilder(userId, ACCESS, accessTokenExpiration)
                .claim(ROLE_CLAIM, role.name())
                .compact();
    }

    public String createRefreshToken(Long userId) {
        return tokenBuilder(userId, REFRESH, refreshTokenExpiration)
                .compact();
    }

    private JwtBuilder tokenBuilder(Long userId, String type, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(userId))                  // sub: 누구의 토큰인지
                .claim(TYPE_CLAIM, type)                          // access인지 refresh인지
                .id(UUID.randomUUID().toString())                 // 같은 순간에 만들어도 토큰이 겹치지 않게
                .issuedAt(now)                                    // iat: 발급 시각
                .expiration(new Date(now.getTime() + expiration)) // exp: 만료 시각
                .signWith(secretKey, Jwts.SIG.HS256);             // 비밀키로 서명 (HS256)
    }

    // 토큰 검증하기
    // 서명이 틀리거나, 만료됐거나, 종류가 다르면 JwtException이 발생합니다.
    public AccessTokenPayload parseAccessToken(String token) {
        Claims claims = parseClaims(token, ACCESS);

        String role = claims.get(ROLE_CLAIM, String.class);
        if (role == null) {
            throw new JwtException("역할 정보가 없는 토큰입니다.");
        }

        return new AccessTokenPayload(Long.valueOf(claims.getSubject()), Role.valueOf(role));
    }

    public Long getUserIdFromRefreshToken(String token) {
        return Long.valueOf(parseClaims(token, REFRESH).getSubject());
    }

    private Claims parseClaims(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)   // 여기서 서명과 만료를 검사합니다.
                .getPayload();

        if (!expectedType.equals(claims.get(TYPE_CLAIM, String.class))) {
            throw new JwtException("토큰 종류가 올바르지 않습니다.");
        }

        return claims;
    }
}
