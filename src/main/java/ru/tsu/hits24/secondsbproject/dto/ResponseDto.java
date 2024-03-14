package ru.tsu.hits24.secondsbproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ResponseDto{

    private String status;

    private String message;
}
