package com.group.jwtexample.domain.user.dto;

import com.group.jwtexample.domain.user.entity.Role;
import com.group.jwtexample.domain.user.entity.User;

// Admin 설정
public record AdminMeResponse(
        Long id,
        String email,
        boolean isAdmin
) { public static AdminMeResponse from(User user) {
        return new AdminMeResponse(
                user.getId(),
                user.getEmail(),
                user.getRole() == Role.ADMIN
        );
    }
}