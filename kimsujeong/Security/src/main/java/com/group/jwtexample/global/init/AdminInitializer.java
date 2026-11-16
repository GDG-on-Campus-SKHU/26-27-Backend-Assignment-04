package com.group.jwtexample.global.init;

import com.group.jwtexample.domain.user.entity.User;
import com.group.jwtexample.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            userRepository.save(User.createAdmin("admin@gmail.com", passwordEncoder.encode("test123")));
        }
    }
}
