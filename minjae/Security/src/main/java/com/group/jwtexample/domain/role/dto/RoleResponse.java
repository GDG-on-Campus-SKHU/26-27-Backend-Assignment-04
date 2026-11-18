package com.group.jwtexample.domain.role.dto;

import com.group.jwtexample.domain.role.entity.Permission;
import com.group.jwtexample.domain.role.entity.Role;

import java.util.Set;

public record RoleResponse(
        Long id,
        String name,
        Set<Permission> permissions
) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                Set.copyOf(role.getPermissions())
        );
    }
}
