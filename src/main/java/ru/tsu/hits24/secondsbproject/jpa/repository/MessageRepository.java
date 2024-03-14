package ru.tsu.hits24.secondsbproject.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tsu.hits24.secondsbproject.jpa.entity.MessageEntity;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
