package ru.tsu.hits24.secondsbproject.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tsu.hits24.secondsbproject.jpa.entity.MessageEntity;

import java.time.LocalDateTime;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    Page<MessageEntity> findByTopicId(Long topicId, Pageable pageable);

    @Query("SELECT m FROM MessageEntity m " +
            "WHERE (:text IS NULL OR m.content LIKE %:text%) " +
            "AND (:startDate IS NULL OR m.creationTime >= :startDate) " +
            "AND (:endDate IS NULL OR m.creationTime <= :endDate) " +
            "AND (:author IS NULL OR m.creator.username = :author) " +
            "AND (:topicId IS NULL OR m.topic.id = :topicId) " +
            "AND (:categoryId IS NULL OR m.topic.category.id = :categoryId)")
    Page<MessageEntity> searchMessages(
            @Param("text") String text,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("author") String author,
            @Param("topicId") Long topicId,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}
