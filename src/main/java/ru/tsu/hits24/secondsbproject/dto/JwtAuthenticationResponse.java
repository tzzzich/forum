package ru.tsu.hits24.secondsbproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;

}