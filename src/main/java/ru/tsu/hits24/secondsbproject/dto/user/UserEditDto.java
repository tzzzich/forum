package ru.tsu.hits24.secondsbproject.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class UserEditDto  {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, message = "Username must be at least 1 character long")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, message = "Username must be at least 1 character long")
    private String username;

    //date not later than today validation
    private Date birthDay;
}
