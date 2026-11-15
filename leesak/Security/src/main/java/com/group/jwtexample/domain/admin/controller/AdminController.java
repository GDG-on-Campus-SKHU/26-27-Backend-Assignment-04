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

    @GetMapping("/me")
    public AdminResponse getMyInfo(
            @AuthenticationPrincipal Long userId
    ) {
        return adminService.getMyInfo(userId);
    }
}
