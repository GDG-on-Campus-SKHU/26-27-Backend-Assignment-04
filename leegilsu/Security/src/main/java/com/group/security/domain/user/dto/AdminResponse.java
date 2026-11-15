package com.group.security.domain.user.dto;

import com.group.security.domain.user.entity.User;
import com.group.security.domain.user.entity.UserRole;

public record AdminResponse(
        Long id,
        String email,
        boolean isAdmin
) {
    public static AdminResponse from(User user) {
        return new AdminResponse(
                user.getId(),
                user.getEmail(),
                user.getRole() == UserRole.ADMIN
        );
    }
}
