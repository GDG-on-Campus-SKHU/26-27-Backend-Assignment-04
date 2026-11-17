package com.group.jwtexample.global.jwt;

import com.group.jwtexample.domain.user.entity.Role;
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

    public String createAccessToken(Long userId, Role role) {
        return createToken(userId, ACCESS, accessTokenExpiration, role);    // role 역할 추가로 유저 구분
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH, refreshTokenExpiration, null);
    }

    private String createToken(Long userId, String type, long expiration, Role role) {
        Date now = new Date();

        var builder = Jwts.builder();

        builder.subject(String.valueOf(userId))
                .claim("type", type)                           // access인지 refresh인지
                .id(UUID.randomUUID().toString())                 // 같은 순간에 만들어도 토큰이 겹치지 않게
                .issuedAt(now)                                    // iat: 발급 시각
                .expiration(new Date(now.getTime() + expiration)); // exp: 만료 시각

        if (ACCESS.equals(type)) {
            builder.claim("role", role.name());
        }
        return builder.signWith(secretKey, Jwts.SIG.HS256)      // 비밀키로 서명 (HS256)
                .compact();
    }

    // 토큰 검증하기
    // 서명이 틀리거나, 만료됐거나, 종류가 다르면 JwtException이 발생합니다.
    public Long getUserIdFromAccessToken(String token) {
        return getUserId(token, ACCESS);
    }

    public String getRoleFromAccessToken(String token) {
        Claims claims = parseClaims(token, ACCESS);
        return claims.get("role", String.class);
    }

    public AccessTokenInfo getAccessTokenInfo(String token) { // 토큰 정보 레코드 받기
        Claims claims = parseClaims(token, ACCESS);

        Long userId = Long.valueOf(claims.getSubject());
        String roleValue = claims.get("role", String.class);

        if (roleValue == null) {
            throw new IllegalArgumentException("역할이 없습니다.");
        }

        Role role = Role.valueOf(roleValue);
        return new AccessTokenInfo(userId, role);
    }

    public Long getUserIdFromRefreshToken(String token) {
        return getUserId(token, REFRESH);
    }

        //토큰의 서명, 만료 시간, 종류를 검사하고 내용을 반환
        private Claims parseClaims (String token, String expectedType){
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!expectedType.equals(claims.get("type", String.class))) {
                throw new JwtException("토큰 종류가 올바르지 않습니다.");
            }
            return claims;
        }

        // 검증된 토큰에서 사용자 ID를 꺼내 반환
        private Long getUserId (String token, String expectedType){
            Claims claims = parseClaims(token, expectedType);
            return Long.valueOf(claims.getSubject());
        }
}
