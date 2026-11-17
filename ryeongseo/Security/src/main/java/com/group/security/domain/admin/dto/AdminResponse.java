package com.group.security.domain.admin.dto;

public record AdminResponse(
        Long id,
        String email,
        boolean isAdmin
) {
}
