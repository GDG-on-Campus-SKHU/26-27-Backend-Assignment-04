package com.group.jwtexample.domain.admin.controller;

import com.group.jwtexample.domain.admin.dto.AdminResponse;
import com.group.jwtexample.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // 토큰에서 꺼낸 userId가 @AuthenticationPrincipal로 들어옵니다.
    @GetMapping("/me")
    public AdminResponse getMyInfo(@AuthenticationPrincipal Long userId) {
        return adminService.getMyInfo(userId);
    }
}
