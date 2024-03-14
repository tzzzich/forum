package ru.tsu.hits24.secondsbproject.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "messages")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Setter
@Getter
public class MessageEntity extends ForumEntity{
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "parent_topic_id")
    private TopicEntity topic;
}
