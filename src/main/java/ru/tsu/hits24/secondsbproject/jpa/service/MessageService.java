package ru.tsu.hits24.secondsbproject.jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageCreateDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageEditDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.entity.MessageEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.TopicEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.MessageRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.TopicRopository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final UserService userService;
    private final TopicRopository topicRepository;
    private final MessageRepository messageRepository;

    public Long createMessage(MessageCreateDto data) {
        UserEntity user = userService.getCurrentUser();

        TopicEntity parent = topicRepository.findById(data.getTopicId())
                .orElseThrow(() ->new DatabaseException("Invalid parent topic id"));
        if (parent.getIsArchived() == true) {
            throw new InvalidArgumentsException("Cannot create messages in an archived topic.");
        }


        if (!userService.isAdmin(user) && !userService.isModerator(user) && !userService.isUser(user)){
            throw new PermissionDeniedException("User is not permitted to create messages.");
        }

        Long id;
        MessageEntity message = MessageEntity.builder()
                .content(data.getContent())
                .topic(parent)
                .build();
        message.setCreationTime(LocalDateTime.now());
        message.setModificationTime(LocalDateTime.now());
        message.setCreator(user);
        try {
            id = messageRepository.save(message).getId();
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return id;
    }

    public MessageDto getMessage(Long id) {
        UserEntity user = userService.getCurrentUser();

        MessageEntity message = messageRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid message id"));

        return new MessageDto(message);
    }


     public Page<MessageDto> getMessagesByPage(int pageNumber, int pageSize ,Long topicId) {
         UserEntity user = userService.getCurrentUser();

         Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("creationTime").descending());

         Page<MessageEntity> messagePage;
         if (topicId == null){
             messagePage = messageRepository.findAll(pageable);
         } else {
             messagePage = messageRepository.findByTopicId(topicId, pageable);
         }

         return messagePage.map(this::mapToDto);

    }

    public Page<MessageDto> searchMessages(
            String text,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String author,
            Long topicId,
            Long categoryId,
            int pageNumber,
            int pageSize) {

        UserEntity user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return messageRepository.searchMessages(
                        text,
                        startDate,
                        endDate,
                        author,
                        topicId,
                        categoryId,
                        pageable)
                .map(this::mapToDto);
    }

    private MessageDto mapToDto(MessageEntity entity) {
        return new MessageDto(entity);
    }

    public MessageDto editMessage(MessageEditDto data, Long id) {
        UserEntity user = userService.getCurrentUser();

        MessageEntity message = messageRepository.findById(id)
                .orElseThrow(() -> new DatabaseException("Invalid message id"));

        if (message.getCreator() != user) {
            throw new PermissionDeniedException("User is not permitted to edit this topic.");
        }

        message.setContent(data.getContent());
        message.setModificationTime(LocalDateTime.now());
        try {
            messageRepository.save(message);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new MessageDto(message);

    }

    public ResponseDto deleteMessage(Long id) {
        UserEntity user = userService.getCurrentUser();

        MessageEntity message = messageRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid message id"));

        if (message.getCreator() != user
                && !message.getTopic().getCategory().getModerators().contains(user)
                && !userService.isAdmin(user)) {
            throw new PermissionDeniedException("User is not permitted to delete this message.");
        }

        try {
            messageRepository.delete(message);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new ResponseDto("Success", "Message deleted successfully.");

    }
}
