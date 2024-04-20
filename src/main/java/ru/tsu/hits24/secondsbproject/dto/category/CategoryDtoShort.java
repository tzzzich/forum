package ru.tsu.hits24.secondsbproject.dto.category;

import lombok.Data;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserDto;
import ru.tsu.hits24.secondsbproject.jpa.entity.CategoryEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDtoShort {
    private Long id;

    private String name;

    //private boolean containsTopics;

    public CategoryDtoShort(CategoryEntity entity) {
        this.name = entity.getName();
        this.id = entity.getId();
        //this.containsTopics = entity.isContainsTopics();
    }
}
