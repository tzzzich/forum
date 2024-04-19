package ru.tsu.hits24.secondsbproject.jpa;

import jakarta.transaction.Transactional;
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

import java.util.Arrays;

@Component
public class SetupDataLoader implements
        ApplicationRunner {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Transactional
    public void run(ApplicationArguments args) {
        System.out.println("Setup");
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("MODERATOR");
        createRoleIfNotFound("USER");

        /*RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN");
        UserEntity user = new UserEntity();
        user.setFullName("Test");
        user.setPassword(passwordEncoder.encode("string"));
        user.setEmail("st@ring");
        user.setRoles(Arrays.asList(adminRole));
        user.setIsBanned(false);
        user.setUsername("test");
        userRepository.save(user);*/

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
