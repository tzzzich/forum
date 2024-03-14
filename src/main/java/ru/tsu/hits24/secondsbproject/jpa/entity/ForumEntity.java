package ru.tsu.hits24.secondsbproject.jpa.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class ForumEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    @Column(nullable = false)
    private LocalDateTime modificationTime;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private UserEntity creator;
}
