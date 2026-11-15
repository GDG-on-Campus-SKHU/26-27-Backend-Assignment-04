package com.group.security.global.config;

import com.group.security.domain.user.entity.User;
import com.group.security.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "test1234@test.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.createAdmin(adminEmail, passwordEncoder.encode("test1234!"));
            userRepository.save(admin);
        }
    }
}
