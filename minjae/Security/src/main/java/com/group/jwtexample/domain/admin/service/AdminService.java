package com.group.jwtexample.domain.admin.service;

import com.group.jwtexample.domain.admin.dto.AdminResponse;
import com.group.jwtexample.domain.user.entity.User;
import com.group.jwtexample.domain.user.repository.UserRepository;
import com.group.jwtexample.global.exception.ErrorCode;
import com.group.jwtexample.global.exception.NotFoundException;
import com.group.jwtexample.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final UserRepository userRepository;

    public AdminResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (user.getIsAdmin()) {
            return AdminResponse.from(user);
        } else  {
            throw new UnauthorizedException(ErrorCode.ADMIN_REQUIRED);
        }
    }

    public Boolean userIsAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return user.getIsAdmin();
    }
}
