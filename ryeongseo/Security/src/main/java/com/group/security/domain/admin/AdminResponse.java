package com.group.security.domain.admin;

public record AdminResponse(
        Long id,
        String email,
        boolean isAdmin
) {
}
