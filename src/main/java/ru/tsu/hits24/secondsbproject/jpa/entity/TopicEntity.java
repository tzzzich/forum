package ru.tsu.hits24.secondsbproject.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "topics")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Setter
@Getter
@Builder
public class TopicEntity extends ForumEntity{

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isArchived;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "topic")
    private List<MessageEntity> messages;
}
