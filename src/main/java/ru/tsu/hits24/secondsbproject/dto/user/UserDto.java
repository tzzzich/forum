package ru.tsu.hits24.secondsbproject.dto.user;

import lombok.*;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class UserDto {

    private Long id;

    private String fullName;

    private String email;

    private String password;

    private String phoneNumber;

    private String username;

    private List<RoleDto> Roles;

    public UserDto(UserEntity user) {
        this.id = user.getId();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.id = user.getId();
        this.Roles = user.getRoles().stream()
                .map(role -> new RoleDto(role))
                .collect(Collectors.toList());
        this.username = user.getUsername();
    }
}
