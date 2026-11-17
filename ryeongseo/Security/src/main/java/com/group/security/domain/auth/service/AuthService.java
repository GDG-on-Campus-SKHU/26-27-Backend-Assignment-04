
package com.group.security.domain.auth.service;

import com.group.security.domain.auth.dto.LoginRequest;
import com.group.security.domain.auth.dto.RefreshRequest;
import com.group.security.domain.auth.dto.SignupRequest;
import com.group.security.domain.auth.dto.TokenResponse;
import com.group.security.domain.auth.entity.RefreshToken;
import com.group.security.domain.auth.repository.RefreshTokenRepository;
import com.group.security.domain.user.entity.User;
import com.group.security.domain.user.repository.UserRepository;
import com.group.security.global.exception.ConflictException;
import com.group.security.global.exception.ErrorCode;
import com.group.security.global.exception.UnauthorizedException;
import com.group.security.global.jwt.JwtProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.group.security.global.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    public void signup(SignupRequest request) {
        String email = request.email();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL);
        }

        try {
            userRepository.saveAndFlush(
                    new User(
                            email,
                            passwordEncoder.encode(request.password())
                    )
            );
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL, e);
        }
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS);
        }

        return issueTokens(user);
    }

    // Refresh Token으로 토큰 재발급
    public TokenResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        Long userId;
        try {
            userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_REUSED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        return issueTokens(user);
    }

    // Access Token과 Refresh Token 발급
    private TokenResponse issueTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getRole()
        );

        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId())
                .orElse(new RefreshToken(user.getId(), refreshToken));

        savedToken.updateToken(refreshToken);
        refreshTokenRepository.save(savedToken);

        return TokenResponse.of(accessToken, refreshToken);
    }
}
