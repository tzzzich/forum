package ru.tsu.hits24.secondsbproject.service;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tsu.hits24.secondsbproject.dto.JwtAuthenticationResponse;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserCreateDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserEditDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserProfileDto;
import ru.tsu.hits24.secondsbproject.exception.DuplicateEmailException;
import ru.tsu.hits24.secondsbproject.exception.DuplicateUsernameException;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.entity.CategoryEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.Role;
import ru.tsu.hits24.secondsbproject.jpa.entity.RoleEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.CategoryRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.RoleRepository;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public ResponseDto createUser(@Valid @NonNull UserCreateDto data) {

        UserEntity admin = userService.getCurrentUser();
        if (!userService.isAdmin(admin)) {
            throw new PermissionDeniedException("User doesn't have the rights to edit other users.");
        }

        RoleEntity[] basicRoles = {userService.getBasicRole()};

        System.out.println("Create user method");
        UserEntity user = UserEntity.builder()
                .fullName(data.getFullName())
                .username(data.getUsername())
                .email(data.getEmail())
                .password(passwordEncoder.encode(data.getPassword()))
                .phoneNumber(data.getPhoneNumber())
                .roles(new ArrayList<RoleEntity>(Arrays.asList(basicRoles)))
                .isBanned(false)
                .isEnabled(true)
                .build();

        try {
            userService.areDetailsUnique(user);
            userService.save(user);
        } catch (DuplicateUsernameException | DuplicateEmailException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new ResponseDto("Success", "User created successfully");
    }

    public UserProfileDto editUser (@NonNull Long id, @Valid UserEditDto data) {
        UserEntity admin = userService.getCurrentUser();
        if (!userService.isAdmin(admin)) {
            throw new PermissionDeniedException("User doesn't have the rights to edit other users.");
        }

        UserEntity user = userService.findById(id);
        if (user == null) {
            throw new InvalidArgumentsException("Invalid user id");
        }
        user.setFullName(data.getFullName());
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setPhoneNumber(data.getPhoneNumber());

        try {
            userService.areDetailsUnique(user);
            userService.save(user);
        } catch (DuplicateUsernameException | DuplicateEmailException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new UserProfileDto(user);
    }

    @Transactional
    public ResponseDto restoreUser(@NonNull Long id) {
        UserEntity admin = userService.getCurrentUser();
        if (!userService.isAdmin(admin)) {
            throw new PermissionDeniedException("User doesn't have the rights to edit other users.");
        }

        UserEntity deletedUser = userService.findById(id);
        if (deletedUser == null) {
            throw new InvalidArgumentsException("Invalid user id.");
        }

        deletedUser.setIsEnabled(true);

        try {
            userService.save(deletedUser);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new ResponseDto("Success", "User with id:" + deletedUser.getId()
                + "successfully restored.");
    }

    @Transactional
    public ResponseDto deleteUser(@NonNull Long id) {
        UserEntity admin = userService.getCurrentUser();
        if (!userService.isAdmin(admin)) {
            throw new PermissionDeniedException("User doesn't have the rights to edit other users.");
        }

        UserEntity deletedUser = userService.findById(id);
        if (deletedUser == null) {
            throw new InvalidArgumentsException("Invalid user id.");
        }
        if (userService.isAdmin(deletedUser)) {
            throw new PermissionDeniedException("Can't delete admins.");
        }

        deletedUser.setIsEnabled(false);

        try {
            userService.save(deletedUser);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new ResponseDto("Success", "User with id:" + deletedUser.getId()
                + "successfully deleted.");
    }

    @Transactional
    public ResponseDto banUser(Long id) {
        UserEntity admin = userService.getCurrentUser();
        if (!userService.isAdmin(admin)) {
            throw new PermissionDeniedException("User doesn't have the rights to edit other users.");
        }

        UserEntity bannedUser = userService.findById(id);
        if (bannedUser == null) {
            throw new InvalidArgumentsException("Invalid user id.");
        }
        if (userService.isAdmin(bannedUser)) {
            throw new PermissionDeniedException("Can't ban admins.");
        }

        bannedUser.setIsBanned(true);

        try {
            userService.save(bannedUser);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new ResponseDto("Success", "User with id:" + bannedUser.getId()
                + "successfully banned.");
    }

    @Transactional
    public ResponseDto unbanUser(Long id) {

        UserEntity admin = userService.getCurrentUser();
        if (!userService.isAdmin(admin)) {
            throw new PermissionDeniedException("User doesn't have the rights to edit other users.");
        }

        UserEntity bannedUser = userService.findById(id);
        if (bannedUser == null) {
            throw new InvalidArgumentsException("Invalid user id.");
        }

        bannedUser.setIsBanned(false);

        try {
            userService.save(bannedUser);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new ResponseDto("Success", "User with id:" + bannedUser.getId()
                + "successfully unbanned.");
    }

    @Transactional
    public ResponseDto setUserRole (@NonNull Long id, @NonNull Role role) {
        UserEntity admin = userService.getCurrentUser();
        if (!userService.isAdmin(admin)) {
            throw new PermissionDeniedException("User doesn't have the rights to edit other users.");
        }

        UserEntity user = userService.findById(id);
        if (user == null) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        RoleEntity roleEntity = roleRepository.findByName(role.toString());
        if (roleEntity == null) {
            throw new InvalidArgumentsException("Invalid role value");
        }

        log.info("role: {}", roleEntity);

        user.getRoles().add(roleEntity);
        try {
            userService.save(user);
        }
        catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
        System.out.println(user.getRoles());

        return new ResponseDto("Success", "Successfully added role" +
                role + "to user with id" + id);
    }

    @Transactional
    public ResponseDto removeUserRole (@NonNull Long id, @NonNull Role role) {
        UserEntity admin = userService.getCurrentUser();
        if (!userService.isAdmin(admin)) {
            throw new PermissionDeniedException("User doesn't have the rights to edit other users.");
        }

        UserEntity user = userService.findById(id);
        if (user == null) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        RoleEntity roleEntity = roleRepository.findByName(role.toString());
        if (roleEntity == null) {
            throw new InvalidArgumentsException("Invalid role value");
        }

        if (!user.getRoles().contains(roleEntity)) {
            throw new InvalidArgumentsException("User with id " + id
            + "does not have the role" + role);
        }

        log.info("role: {}", roleEntity);

        user.getRoles().remove(roleEntity);
        try {
            userService.save(user);
        }
        catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
        System.out.println(user.getRoles());

        return new ResponseDto("Success", "Successfully removed role" +
                role + "from user with id" + id);
    }

    @Transactional
    public CategoryDto setModerator(Long categoryId, Long moderId) {
        UserEntity user = userService.getCurrentUser();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->new DatabaseException("Invalid category id"));
        UserEntity moder = userService.findById(moderId);
        if (moder == null) {
            throw new DatabaseException("Invalid user id");
        }
        if (!userService.isAdmin(user) ){
            throw new PermissionDeniedException("User is not permitted to set moderators.");
        }

        userService.setModerator(moder);

        category.getModerators().add(moder);

        try {
            categoryRepository.save(category);
            userService.save(moder);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new CategoryDto(category);
    }

    @Transactional
    public CategoryDto removeModerator(Long categoryId, Long moderId) {
        UserEntity user = userService.getCurrentUser();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->new DatabaseException("Invalid category id"));
        UserEntity moder = userService.findById(moderId);
        if (moder == null) {
            throw new DatabaseException("Invalid user id");
        }
        if (!userService.isAdmin(user) ){
            throw new PermissionDeniedException("User is not permitted set moderators.");
        }

        if (!category.getModerators().contains(moder)) {
            throw new InvalidArgumentsException("User with id:" + moderId
                    + "is not a moderator of a category:" + categoryId +".");
        }

        category.getModerators().remove(moder);

        try {
            categoryRepository.save(category);
            userService.save(moder);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new CategoryDto(category);
    }


}
