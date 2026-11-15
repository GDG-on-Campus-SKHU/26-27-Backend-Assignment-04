package com.group.security.domain.user.service;

import com.group.security.domain.user.dto.AdminResponse;
import com.group.security.domain.user.entity.User;
import com.group.security.domain.user.repository.UserRepository;
import com.group.security.global.exception.ErrorCode;
import com.group.security.global.exception.NotFoundException;
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

        return AdminResponse.from(user);
    }
}
