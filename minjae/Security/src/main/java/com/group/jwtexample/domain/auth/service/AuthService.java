package com.group.jwtexample.domain.auth.service;

import com.group.jwtexample.domain.auth.dto.LoginRequest;
import com.group.jwtexample.domain.auth.dto.RefreshRequest;
import com.group.jwtexample.domain.auth.dto.SignupRequest;
import com.group.jwtexample.domain.auth.dto.TokenResponse;
import com.group.jwtexample.domain.auth.entity.RefreshToken;
import com.group.jwtexample.domain.auth.repository.RefreshTokenRepository;
import com.group.jwtexample.domain.role.entity.Permission;
import com.group.jwtexample.domain.role.entity.Role;
import com.group.jwtexample.domain.role.repository.RoleRepository;
import com.group.jwtexample.domain.user.entity.User;
import com.group.jwtexample.domain.user.repository.UserRepository;
import com.group.jwtexample.global.exception.ConflictException;
import com.group.jwtexample.global.exception.ErrorCode;
import com.group.jwtexample.global.exception.UnauthorizedException;
import com.group.jwtexample.global.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private final RoleRepository roleRepository;

    public void signup(SignupRequest request) {
        String email = request.email();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL);
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new IllegalStateException("기본 USER 역할이 없습니다."));

        try {
            User user = new User(
                    email,
                    passwordEncoder.encode(request.password())
            );

            user.addRole(userRole);
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL, e);
        }
    }

    public TokenResponse login(LoginRequest request) {
        // 1. 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS);
        }

        EnumSet<Permission> permissions = collectPermissions(user);

        // 3. 토큰 발급
        return issueTokens(user.getId(), permissions);
    }

    public TokenResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        // 1. Refresh Token 자체가 올바른지 확인 (서명, 만료, 종류)
        Claims claims;
        Long userId;
        try {
            claims = jwtProvider.getClaimsFromRefreshToken(refreshToken);
            userId = Long.valueOf(claims.getSubject());

        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. DB에 저장된 최신 Refresh Token과 같은지 확인
        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_REUSED);
        }

        // 3. 사용자의 현재 역할과 권한 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN));

        EnumSet<Permission> permissions = collectPermissions(user);

        // 3. 새 토큰 발급 (이전 Refresh Token은 덮어써져서 더 이상 못 씀)
        return issueTokens(userId, permissions);
    }

    private TokenResponse issueTokens(Long userId, Set<Permission> permissions) {
        String accessToken = jwtProvider.createAccessToken(userId, permissions);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // 처음 로그인이면 새로 저장하고, 이미 있으면 새 토큰으로 덮어씁니다.
        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken(userId, refreshToken));

        savedToken.updateToken(refreshToken);
        refreshTokenRepository.save(savedToken);

        return TokenResponse.of(accessToken, refreshToken);
    }

    private EnumSet<Permission> collectPermissions(User user) {
        return  user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .collect(Collectors.toCollection(
                        () -> EnumSet.noneOf(Permission.class)
                ));
    }
}
