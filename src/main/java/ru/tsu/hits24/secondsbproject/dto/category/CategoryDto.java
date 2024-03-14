package ru.tsu.hits24.secondsbproject.dto.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDtoShort;
import ru.tsu.hits24.secondsbproject.dto.user.UserDto;
import ru.tsu.hits24.secondsbproject.dto.user.UserProfileDto;
import ru.tsu.hits24.secondsbproject.jpa.entity.CategoryEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.TopicEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDto {

    private Long id;

    private LocalDateTime creationTime;

    private LocalDateTime modificationTime;

    private String creatorEmail;

    private String name;

    private boolean containsTopics;

    private Long parentCategory;

    private List<CategoryDtoShort> subcategories;

    private List<TopicDtoShort> topics;

    private List<UserDto> moderators;

    public CategoryDto(CategoryEntity entity) {
        this.name = entity.getName();
        this.id = entity.getId();
        this.creationTime = entity.getCreationTime();
        this.modificationTime = entity.getModificationTime();
        this.creatorEmail = entity.getCreator().getEmail();
        this.containsTopics = entity.isContainsTopics();
        if (entity.getParentCategory() != null) {
            this.parentCategory = entity.getParentCategory().getId();
        }
        this.subcategories = entity.getSubcategories().stream().map(c -> new CategoryDtoShort(c)).toList();
        this.topics = entity.getTopics().stream().map(t -> new TopicDtoShort(t)).toList();
        this.moderators = entity.getModerators().stream().map(m -> new UserDto(m)).toList();
    }
}
