package com.group.jwtexample.global.jwt;

import com.group.jwtexample.domain.user.entity.Role;

public record AccessTokenInfo(Long userId, Role role) {
}
