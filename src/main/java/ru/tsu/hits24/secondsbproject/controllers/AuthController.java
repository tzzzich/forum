package ru.tsu.hits24.secondsbproject.controllers;

import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.tsu.hits24.secondsbproject.dto.JwtAuthenticationResponse;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.ValidationErrorDto;
import ru.tsu.hits24.secondsbproject.dto.user.RefreshJwtRequest;
import ru.tsu.hits24.secondsbproject.dto.user.UserCreateDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserLoginDto;
import ru.tsu.hits24.secondsbproject.exception.DuplicateEmailException;
import ru.tsu.hits24.secondsbproject.exception.DuplicateUsernameException;
import ru.tsu.hits24.secondsbproject.service.UserAuthService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserAuthService userAuthService;

    @PostMapping("login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody UserLoginDto authRequest) {
        log.info("Initiating method login");
        final JwtAuthenticationResponse token = userAuthService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("token")
    public ResponseEntity<JwtAuthenticationResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtAuthenticationResponse token = userAuthService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtAuthenticationResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        final JwtAuthenticationResponse token = userAuthService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("register")
    @ResponseBody
    public ResponseEntity<JwtAuthenticationResponse> createUser(@Valid @RequestBody UserCreateDto data) {
        JwtAuthenticationResponse token = userAuthService.register(data);
        return ResponseEntity.ok(token);
    }


}
