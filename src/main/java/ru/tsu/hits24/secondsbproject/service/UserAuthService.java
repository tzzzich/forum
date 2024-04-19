package ru.tsu.hits24.secondsbproject.service;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tsu.hits24.secondsbproject.JwtAuthentication;
import ru.tsu.hits24.secondsbproject.dto.JwtAuthenticationResponse;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserCreateDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserEditDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserLoginDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserProfileDto;
import ru.tsu.hits24.secondsbproject.exception.DuplicateEmailException;
import ru.tsu.hits24.secondsbproject.exception.DuplicateUsernameException;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {
    private final UserService userService;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;

    //private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse register(@Valid @NonNull UserCreateDto data) {
        System.out.println("Register method");
        UserEntity user = UserEntity.builder()
                .fullName(data.getFullName())
                .username(data.getUsername())
                .email(data.getEmail())
                .password(data.getPassword())
                .phoneNumber(data.getPhoneNumber())
                .roles(userService.getBasicRole())
                .isBanned(false)
                .build();

        try {
            userService.areDetailsUnique(user);
            userService.save(user);
        } catch (DuplicateUsernameException | DuplicateEmailException e) {
            throw e;
        }
        final String accessToken = jwtProvider.generateAccessToken(user);
        final String refreshToken = jwtProvider.generateRefreshToken(user);
        //System.out.println(accessToken);
        refreshStorage.put(user.getUsername(), refreshToken);
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    public JwtAuthenticationResponse login(@NonNull UserLoginDto authRequest) {
        UserEntity user;
        try {
            user = userService.getByUsername(authRequest.getUsername());
        } catch (Exception ex) {
            throw new UsernameNotFoundException("User not found");
        }
        if ((authRequest.getPassword()==user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        final String accessToken = jwtProvider.generateAccessToken(user);
        final String refreshToken = jwtProvider.generateRefreshToken(user);
        refreshStorage.put(user.getUsername(), refreshToken);
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    public UserProfileDto getProfile() {
        log.info("Get Profile method");
        UserEntity user = userService.getCurrentUser();
        System.out.println(user);
        return new UserProfileDto(user);
    }

    public JwtAuthenticationResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserEntity user = userService.getByUsername(username);
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtAuthenticationResponse(accessToken, null);
            }
        }
        return new JwtAuthenticationResponse(null, null);
    }

    public JwtAuthenticationResponse refresh(@NonNull String refreshToken)  {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserEntity user = userService.getByUsername(username);
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getUsername(), newRefreshToken);
                return new JwtAuthenticationResponse(accessToken, newRefreshToken);
            }
        }
        throw new BadCredentialsException("Invalid JWT token");
    }
    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public UserProfileDto editUser(UserEditDto data) {

        UserEntity user = userService.getCurrentUser();
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

        return new UserProfileDto(user);
    }

    public ResponseDto banUser(Long id) {

        UserEntity user = userService.getCurrentUser();

        UserEntity bannedUser = userService.findById(id);
        if (bannedUser == null) {
            throw new InvalidArgumentsException("Invalid user id.");
        }

        if (!userService.isAdmin(user)) {
            throw new PermissionDeniedException("User doesn't have the rights to ban other users.");
        }

        if (userService.isAdmin(bannedUser)) {
            throw new PermissionDeniedException("Can't ban admins.");
        }

        bannedUser.setIsBanned(true);

        try {
            userService.save(bannedUser);
        } catch (Exception e) {
            throw e;
        }

        return new ResponseDto("Success", "User with id:" + bannedUser.getId()
                + "successfully banned.");
    }

    public ResponseDto unbanUser(Long id) {

        UserEntity user = userService.getCurrentUser();

        UserEntity bannedUser = userService.findById(id);
        if (bannedUser == null) {
            throw new InvalidArgumentsException("Invalid user id.");
        }

        if (!userService.isAdmin(user)) {
            throw new PermissionDeniedException("User doesn't have the rights to unban other users.");
        }

        bannedUser.setIsBanned(false);

        try {
            userService.save(bannedUser);
        } catch (Exception e) {
            throw e;
        }

        return new ResponseDto("Success", "User with id:" + bannedUser.getId()
                + "successfully unbanned.");
    }



}
