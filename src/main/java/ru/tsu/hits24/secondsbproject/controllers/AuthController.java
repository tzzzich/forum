package ru.tsu.hits24.secondsbproject.controllers;

import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.tsu.hits24.secondsbproject.JwtAuthentication;
import ru.tsu.hits24.secondsbproject.dto.JwtAuthenticationResponse;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.ValidationErrorDto;
import ru.tsu.hits24.secondsbproject.dto.user.RefreshJwtRequest;
import ru.tsu.hits24.secondsbproject.dto.user.UserCreateDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserLoginDto;
import ru.tsu.hits24.secondsbproject.exception.DuplicateEmailException;
import ru.tsu.hits24.secondsbproject.exception.DuplicateUsernameException;
import ru.tsu.hits24.secondsbproject.service.UserAuthService;

import java.io.Console;
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

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ResponseDto> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseDto("Conflict", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ResponseDto> handleDuplicateEmailException(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseDto("Conflict", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        ValidationErrorDto errorDto = new ValidationErrorDto(errors);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ResponseDto> handleDatabaseException (DatabaseException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ResponseDto> handleException(AuthException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDto> handleBadCredentials (BadCredentialsException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDto> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto("Not found", ex.getMessage()));
    }
}
