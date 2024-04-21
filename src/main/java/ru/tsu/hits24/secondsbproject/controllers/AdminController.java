package ru.tsu.hits24.secondsbproject.controllers;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tsu.hits24.secondsbproject.dto.JwtAuthenticationResponse;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserCreateDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserEditDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserProfileDto;
import ru.tsu.hits24.secondsbproject.jpa.entity.Role;
import ru.tsu.hits24.secondsbproject.service.AdminService;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("user/create")
    @ResponseBody
    public ResponseEntity<ResponseDto> createUser(@Valid @RequestBody UserCreateDto data) {
        ResponseDto responseDto = adminService.createUser(data);
        return ResponseEntity.ok(responseDto);
    }
    @PatchMapping("user/edit")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<UserProfileDto> editUser(@RequestParam Long id, @Valid @RequestBody UserEditDto data) {
        UserProfileDto user = adminService.editUser(id, data);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("user/delete")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> deleteUser(@RequestParam Long id) {
        ResponseDto responseDto = adminService.deleteUser(id);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("user/setRole")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> setRoleUser(@RequestParam Long id, @RequestParam Role role) {
        ResponseDto responseDto = adminService.setUserRole(id, role);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("user/removeRole")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> removeRoleUser(@RequestParam Long id, @RequestParam Role role) {
        ResponseDto responseDto = adminService.removeUserRole(id, role);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("user/restore")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> restoreUser(@RequestParam Long id) {
        ResponseDto responseDto = adminService.restoreUser(id);
        return ResponseEntity.ok(responseDto);
    }
    @PatchMapping("user/ban")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> banUser(@RequestParam Long id) {
        ResponseDto response = adminService.banUser(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("user/unban")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> unbanUser(@RequestParam Long id) {
        ResponseDto response = adminService.unbanUser(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("category/moderator/set")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<CategoryDto> setModerator(@RequestParam Long categoryId, @RequestParam Long moderatorId) {
        CategoryDto response = adminService.setModerator(categoryId, moderatorId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("category/moderator/delete")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<CategoryDto> removeModerator(@RequestParam Long categoryId, @RequestParam Long moderatorId) {
        CategoryDto response = adminService.removeModerator(categoryId, moderatorId);
        return ResponseEntity.ok(response);
    }
}
