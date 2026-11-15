package com.group.security.domain.admin;

import com.group.security.domain.user.entity.User;
import com.group.security.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public AdminResponse getMyInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new AdminResponse(
                user.getId(),
                user.getEmail(),
                true
        );
    }
}
