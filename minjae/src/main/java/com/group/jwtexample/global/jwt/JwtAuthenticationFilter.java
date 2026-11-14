package com.group.jwtexample.global.jwt;

import com.group.jwtexample.domain.user.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 토큰 꺼내기
        String token = resolveToken(request);

        if (token != null) {
            try {
                // 2. 토큰 검증하고 userId 꺼내기
                Long userId = jwtProvider.getUserIdFromAccessToken(token);

                // 3. "이 요청은 userId 사용자가 보냈다"는 인증 정보 만들기

                // 3-1. 관리자인지 확인 후 역할 넣어주기
                Boolean userIsAdmin = userService.userIsAdmin(userId);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

                if (userIsAdmin) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                Authentication authentication =
                        UsernamePasswordAuthenticationToken.authenticated(
                                userId,
                                null,
                                authorities
                        );

                // 4. SecurityContext에 저장 → 이제 Spring Security가 "인증된 요청"으로 봅니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {
                // 잘못된 토큰(위조, 만료 등)이면 인증 정보를 만들지 않고 그냥 넘어갑니다.
                // → 보호된 API라면 뒤에서 Spring Security가 401을 응답합니다.
            }
        }

        // 5. 다음 필터로 넘기기 (토큰이 없거나 잘못됐으면 인증 정보 없이 넘어갑니다)
        filterChain.doFilter(request, response);
    }

    // "Authorization: Bearer xxxxx" 에서 xxxxx 부분만 꺼냅니다.
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " (공백 포함 7글자) 이후
        }
        return null;
    }
}
