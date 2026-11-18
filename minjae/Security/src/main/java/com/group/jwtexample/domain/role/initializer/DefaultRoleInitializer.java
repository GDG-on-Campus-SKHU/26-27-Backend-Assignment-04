package com.group.jwtexample.domain.role.initializer;

import com.group.jwtexample.domain.role.entity.Permission;
import com.group.jwtexample.domain.role.entity.Role;
import com.group.jwtexample.domain.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DefaultRoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role user = new Role("USER");
        user.addPermission(Permission.ACCESS_USER);
        roleRepository.save(user);

        Role admin = new Role("ADMIN");
        admin.addPermission(Permission.ACCESS_USER);
        admin.addPermission(Permission.ACCESS_ADMIN);
        roleRepository.save(admin);
    }
}
