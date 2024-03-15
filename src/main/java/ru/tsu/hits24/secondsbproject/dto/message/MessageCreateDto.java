package ru.tsu.hits24.secondsbproject.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageCreateDto {
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, message = "Content must be at least 1 character long")
    private String content;

    @NotNull(message = "Topic cannot be blank")
    private Long topicId;
}
