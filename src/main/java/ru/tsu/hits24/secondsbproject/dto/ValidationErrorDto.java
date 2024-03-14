package ru.tsu.hits24.secondsbproject.dto;

import java.util.Map;

public class ValidationErrorDto {
    private Map<String, String> errors;

    public ValidationErrorDto(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
