package ru.tsu.hits24.secondsbproject.dto.category;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryEditDto {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, message = "Name must be at least 1 character long")
    private String name;

    private boolean containsTopics;
}
