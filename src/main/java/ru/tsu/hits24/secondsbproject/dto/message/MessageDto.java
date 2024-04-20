package ru.tsu.hits24.secondsbproject.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.tsu.hits24.secondsbproject.jpa.entity.MessageEntity;

import java.time.LocalDateTime;

@Data
public class MessageDto {

    private Long id;

    private LocalDateTime creationTime;

    private LocalDateTime modificationTime;

    public String creatorEmail;

    private String content;

    private Long topicId;

    public MessageDto(MessageEntity entity) {
        this.id = entity.getId();
        this.content = entity.getContent();
        this.topicId = entity.getTopic().getId();
        this.creationTime = entity.getCreationTime();
        this.modificationTime = entity.getModificationTime();
        this.creatorEmail = entity.getCreator().getEmail();
    }
}
