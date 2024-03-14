package ru.tsu.hits24.secondsbproject.jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tsu.hits24.secondsbproject.dto.JwtAuthenticationResponse;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.user.*;
import ru.tsu.hits24.secondsbproject.exception.DuplicateEmailException;
import ru.tsu.hits24.secondsbproject.exception.DuplicateUsernameException;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.RoleRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.UserRepository;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse loginUser(UserLoginDto data) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    data.getEmail(),
                    data.getPassword()
            ));
        } catch (Exception e){
            throw new BadCredentialsException(e.getMessage());
        }

        UserEntity user = userService.getByEmail(data.getEmail());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse registerUser(UserCreateDto data) {

        UserEntity user = UserEntity.builder()
                .fullName(data.getFullName())
                .username(data.getUsername())
                .email(data.getEmail())
                .password(passwordEncoder.encode(data.getPassword()))
                .birthDay(data.getBirthDay())
                .roles(userService.getBasicRole())
                .build();

        try {
            userService.areDetailsUnique(user);
            userService.save(user);
        } catch (DuplicateUsernameException | DuplicateEmailException e) {
            throw e;
        }
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);

    }

    public UserProfileDto editUser(UserEditDto data) {

        UserEntity user = userService.getCurrentUser();
        user.setFullName(data.getFullName());
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setBirthDay(data.getBirthDay());

        try {
            userService.areDetailsUnique(user);
            userService.save(user);
        } catch (DuplicateUsernameException | DuplicateEmailException e) {
            throw e;
        }

        return new UserProfileDto(user);
    }

    public UserProfileDto getProfile() {

        UserEntity user = userService.getCurrentUser();

        return new UserProfileDto(user);
    }

}
