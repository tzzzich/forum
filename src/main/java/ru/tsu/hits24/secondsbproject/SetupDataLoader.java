package ru.tsu.hits24.secondsbproject;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tsu.hits24.secondsbproject.jpa.entity.RoleEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.UserRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.RoleRepository;
import ru.tsu.hits24.secondsbproject.service.UserService;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetupDataLoader implements
        ApplicationRunner {

    boolean alreadySetup = false;
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;



    @Transactional
    public void run(ApplicationArguments args) {
        System.out.println("Setup");
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("MODERATOR");
        createRoleIfNotFound("USER");

        System.out.println(userService.getAllRoles());

        UserEntity user = UserEntity
                .builder()
                    .username("admin")
                    .fullName("Admin")
                    .roles(userService.getAllRoles())
                    .password(passwordEncoder.encode("string"))
                    .email("st@ring")
                    .isBanned(false)
                    .isEnabled(true)
                    .build();

        userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    RoleEntity createRoleIfNotFound(String name) {

        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity();
            role.setName(name);
            roleRepository.save(role);
        }
        return role;
    }
}
