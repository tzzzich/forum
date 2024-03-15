package ru.tsu.hits24.secondsbproject.jpa;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tsu.hits24.secondsbproject.jpa.entity.PrivilegeEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.RoleEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.PrivilegeRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.RoleRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.UserRepository;

import java.util.Arrays;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_MODERATOR");
        createRoleIfNotFound("ROLE_USER");

        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN");
        UserEntity user = new UserEntity();
        user.setFullName("Test");
        user.setPassword(passwordEncoder.encode("string"));
        user.setEmail("st@ring");
        user.setRoles(Arrays.asList(adminRole));
        user.setIsBanned(false);
        user.setUsername("test");
        userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    RoleEntity createRoleIfNotFound( String name) {

        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            roleRepository.save(role);
        }
        return role;
    }
}
