package ru.tsu.hits24.secondsbproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tsu.hits24.secondsbproject.exception.DuplicateEmailException;
import ru.tsu.hits24.secondsbproject.exception.DuplicateUsernameException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.entity.Role;
import ru.tsu.hits24.secondsbproject.jpa.entity.RoleEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.RoleRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Transactional
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    public UserEntity getByEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new UsernameNotFoundException("User not found");
        }
        UserEntity user = userRepository.findByEmail(email);
        return user;
    }

    public UserEntity getByUsername(String username) throws UsernameNotFoundException{
        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        UserEntity user = userRepository.findByUsername(username);
        return user;
    }



//    public UserDetailsService userDetailsUsernameService() {
//        return this::getByUsername;
//
//    }
//
//    public UserDetailsService userDetailsEmailService() {
//        return this::getByEmail;
//
//    }

    public UserEntity findById (Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    public UserEntity getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info(SecurityContextHolder.getContext().toString());
        UserEntity user = userRepository.findByUsername(username.toString());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (user.getIsBanned() == true) {
            throw new PermissionDeniedException("User is banned");
        }
        if (user.getIsEnabled() == false) {
            throw new PermissionDeniedException("User is deleted");
        }
        return user;
    }

    public void areDetailsUnique(UserEntity user) {
        if (userRepository.findByUsername(user.getUsername()) != null &&
                userRepository.findByUsername(user.getUsername()) != user) {
            throw new DuplicateUsernameException("User with username: " + user.getUsername() + " already exists");
        }

        if (userRepository.findByEmail(user.getEmail()) != null &&
                userRepository.findByEmail(user.getEmail()) != user) {
            throw new DuplicateEmailException("User with email: " + user.getEmail() + " already exists");
        }
    }

    public RoleEntity getBasicRole() {
        return roleRepository.findByName("USER");
    }

    public RoleEntity getAdminRole() {
        return roleRepository.findByName("ADMIN");
    }
    public RoleEntity getModeratorRole() {
        return roleRepository.findByName("MODERATOR");
    }

    public List<RoleEntity> getAllRoles() {
        RoleEntity[] allRoles = {
            this.getBasicRole(),
            this.getAdminRole(),
            this.getModeratorRole()
        };

       return new ArrayList<RoleEntity>(Arrays.asList(allRoles));
    }
    public Boolean isAdmin(UserEntity user){
        return user.getRoles().contains(roleRepository.findByName("ADMIN"));
    }

    public Boolean isModerator(UserEntity user){
        return user.getRoles().contains(roleRepository.findByName("MODERATOR"));
    }

    public Boolean isUser(UserEntity user){
        return user.getRoles().contains(roleRepository.findByName("USER"));
    }

    public void setModerator(UserEntity user) {
        if (!isModerator(user)) {
            user.getRoles().add(roleRepository.findByName("MODERATOR"));
        }
    }

}
