package ru.tsu.hits24.secondsbproject.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "messages")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Setter
@Getter
@Builder
public class MessageEntity extends ForumEntity{

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "parent_topic_id")
    private TopicEntity topic;
}
