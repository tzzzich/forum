package ru.tsu.hits24.secondsbproject.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;

import java.util.Date;

@Data
public class UserProfileDto  {

    private String fullName;

    private String email;

    private String username;

    //date not later than today validation
    private String phoneNumber;

    public UserProfileDto(UserEntity user) {
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
    }
}