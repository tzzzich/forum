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
import ru.tsu.hits24.secondsbproject.dto.category.CategoryCreateDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicCreateDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDtoShort;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicEditDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.entity.CategoryEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.MessageEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.TopicEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.CategoryRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.TopicRopository;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {
    private final UserService userService;
    private final TopicRopository topicRepository;
    private final CategoryRepository categoryRepository;

    public Long createTopic(TopicCreateDto data) {
        UserEntity user = userService.getCurrentUser();

        CategoryEntity parent = categoryRepository.findById(data.getParentCategory())
                .orElseThrow(() ->new DatabaseException("Invalid parent category id"));
        if (parent.isContainsTopics() == false) {
            throw new InvalidArgumentsException("Category cannot contain topics.");
        }


        if (!userService.isAdmin(user) && !userService.isModerator(user) && !userService.isUser(user)){
            throw new PermissionDeniedException("User is not permitted to create topics.");
        }

        Long id;
        TopicEntity topic = TopicEntity.builder()
                .name(data.getName())
                .category(parent)
                .isArchived(false)
                .messages(new ArrayList< MessageEntity >())
                .build();
        topic.setCreationTime(LocalDateTime.now());
        topic.setModificationTime(LocalDateTime.now());
        topic.setCreator(user);
        try {
            id = topicRepository.save(topic).getId();
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return id;
    }

    public TopicDto getTopic(Long id) {
        UserEntity user = userService.getCurrentUser();

        TopicEntity topic = topicRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid topic id"));

        return new TopicDto(topic);
    }
    public Page<TopicDtoShort> getTopicsByPage(int pageNumber, int pageSize ) {
        UserEntity user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("creationTime").descending());

        Page<TopicEntity> topicPage = topicRepository.findAll(pageable);


        return topicPage.map(this::mapToDto);

    }

    private TopicDtoShort mapToDto(TopicEntity entity) {
        return new TopicDtoShort(entity);
    }

    public TopicDtoShort editTopic(TopicEditDto data, Long id) {
        UserEntity user = userService.getCurrentUser();

        TopicEntity topic = topicRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid topic id"));

        if (topic.getCreator() != user) {
            throw new PermissionDeniedException("User is not permitted to edit this topic.");
        }

        topic.setName(data.getName());
        topic.setModificationTime(LocalDateTime.now());
        try {
            topicRepository.save(topic);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new TopicDtoShort(topic);

    }

    public ResponseDto deleteTopic(Long id) {
        UserEntity user = userService.getCurrentUser();

        TopicEntity topic = topicRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid topic id"));

        if (topic.getCreator() != user && !topic.getCategory().getModerators().contains(user)
                && !userService.isAdmin(user)) {
            throw new PermissionDeniedException("User is not permitted to delete this topic.");
        }

        if (topic.getCreator() != user && !topic.getCategory().getModerators().contains(user)) {
            throw new PermissionDeniedException("User is not permitted to delete this topic.");
        }

//        if (!topic.getMessages().isEmpty()) {
//            throw new InvalidArgumentsException("Cannot delete a non empty topic.");
//        }

        try {
            topicRepository.delete(topic);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new ResponseDto("Success", "Topic deleted successfully.");

    }

    public TopicDtoShort archiveTopic(Long id) {
        UserEntity user = userService.getCurrentUser();

        TopicEntity topic = topicRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid topic id"));

        if (topic.getCreator() != user && !topic.getCategory().getModerators().contains(user)
                && !userService.isAdmin(user)) {
            throw new PermissionDeniedException("User is not permitted to archive this topic.");
        }

        if (topic.getCreator() != user && !topic.getCategory().getModerators().contains(user)) {
            throw new PermissionDeniedException("User is not permitted to archive this topic.");
        }

//        if (!topic.getMessages().isEmpty()) {
//            throw new InvalidArgumentsException("Cannot archive a non empty topic.");
//        }

        topic.setIsArchived(true);
        topic.setModificationTime(LocalDateTime.now());

        try {
            topicRepository.save(topic);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new TopicDtoShort(topic);

    }

    public TopicDtoShort unarchiveTopic(Long id) {
        UserEntity user = userService.getCurrentUser();

        TopicEntity topic = topicRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid topic id"));

        if (topic.getCreator() != user && !topic.getCategory().getModerators().contains(user)
                && !userService.isAdmin(user)) {
            throw new PermissionDeniedException("User is not permitted to unarchive this topic.");
        }

        if (topic.getCreator() != user && !topic.getCategory().getModerators().contains(user)) {
            throw new PermissionDeniedException("User is not permitted to unarchive this topic.");
        }

        topic.setIsArchived(false);
        topic.setModificationTime(LocalDateTime.now());

        try {
            topicRepository.save(topic);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new TopicDtoShort(topic);

    }


}
