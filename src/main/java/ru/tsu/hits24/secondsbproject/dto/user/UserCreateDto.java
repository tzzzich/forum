package ru.tsu.hits24.secondsbproject.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class UserCreateDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, message = "Username must be at least 1 character long")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, message = "Username must be at least 1 character long")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 character long")
    //contain letter validation
    private String password;

    //date not later than today validation
    private String phoneNumber;

}
