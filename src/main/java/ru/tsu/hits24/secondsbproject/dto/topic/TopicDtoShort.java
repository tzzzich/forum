package ru.tsu.hits24.secondsbproject.dto.topic;

import lombok.Data;
import ru.tsu.hits24.secondsbproject.jpa.entity.TopicEntity;

import java.time.LocalDateTime;

@Data
public class TopicDtoShort {
    private Long id;

    private String name;

    private boolean isArchived;

    public TopicDtoShort(TopicEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.isArchived = entity.getIsArchived();
    }
}
