package ru.tsu.hits24.secondsbproject.dto.topic;

import lombok.Data;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TopicDto {

    private Long id;

    private LocalDateTime creationTime;

    private LocalDateTime modificationTime;

    private String creatorEmail;

    private String name;

    private boolean isArchived;

    private Long parentCategory;

}
