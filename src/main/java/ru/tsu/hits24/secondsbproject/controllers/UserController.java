package ru.tsu.hits24.secondsbproject.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.tsu.hits24.secondsbproject.dto.JwtAuthenticationResponse;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.ValidationErrorDto;
import ru.tsu.hits24.secondsbproject.dto.user.*;
import ru.tsu.hits24.secondsbproject.exception.DuplicateEmailException;
import ru.tsu.hits24.secondsbproject.exception.DuplicateUsernameException;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.service.UserAuthService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserAuthService userAuthService;

    @Autowired
    public UserController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<JwtAuthenticationResponse> createUser(@Valid @RequestBody UserCreateDto data) {
        JwtAuthenticationResponse token = userAuthService.registerUser(data);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<JwtAuthenticationResponse> loginUser(@Valid @RequestBody UserLoginDto data) {
        JwtAuthenticationResponse token = userAuthService.loginUser(data);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<UserProfileDto> profileUser() {

        UserProfileDto user = userAuthService.getProfile();
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/edit")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<UserProfileDto> editUser(@Valid @RequestBody UserEditDto data) {
        UserProfileDto user = userAuthService.editUser(data);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/ban")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> banUser(@RequestParam Long id) {
        ResponseDto response = userAuthService.banUser(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unban")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> unbanUser(@RequestParam Long id) {
        ResponseDto response = userAuthService.unbanUser(id);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDto> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto("Not found", ex.getMessage()));
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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDto> handleBadCredentials (BadCredentialsException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
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
    @ExceptionHandler(InvalidArgumentsException.class)
    public ResponseEntity<ResponseDto> handleInvalidArgumentsException (InvalidArgumentsException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ResponseDto> handlePermissionDeniedException (PermissionDeniedException ex) {
        ResponseDto errorResponse = new ResponseDto("Forbidden", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception ex) {
        ResponseDto errorResponse = new ResponseDto("Internal Server Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
