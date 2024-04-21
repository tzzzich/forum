package ru.tsu.hits24.secondsbproject.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.ValidationErrorDto;
import ru.tsu.hits24.secondsbproject.dto.user.*;
import ru.tsu.hits24.secondsbproject.exception.DuplicateEmailException;
import ru.tsu.hits24.secondsbproject.exception.DuplicateUsernameException;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.service.UserAuthService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserAuthService userAuthService;

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

}
