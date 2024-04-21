package ru.tsu.hits24.secondsbproject.controllers;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicCreateDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDtoShort;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicEditDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.service.TopicService;

@Slf4j
@RestController
@RequestMapping("/api/topic")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;
    @PostMapping("create")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Long> createTopic(@Valid @RequestBody TopicCreateDto data){
        Long id = topicService.createTopic(data);
        return ResponseEntity.ok(id);
    }

    @GetMapping("topicId")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<TopicDto> getTopic(@RequestParam Long id) {
        TopicDto response = topicService.getTopic( id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("getAllTopics")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Page<TopicDtoShort>> getAllTopicsByPage(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Page<TopicDtoShort> response = topicService.getTopicsByPage(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("categoriesByName")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Page<TopicDtoShort>> getTopicsByName(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(required = false) String name) {
        Page<TopicDtoShort> response = topicService.getTopicsByPageByName(page, size, name);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("edit")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<TopicDtoShort> editTopic(@Valid @RequestBody TopicEditDto data, @RequestParam Long id) {
        TopicDtoShort response = topicService.editTopic(data, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> deleteTopic(@RequestParam Long id) {
        ResponseDto response = topicService.deleteTopic(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("archive")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<TopicDtoShort> archiveTopic(@RequestParam Long id) {
        TopicDtoShort response = topicService.archiveTopic(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("unarchive")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<TopicDtoShort> unarchiveTopic(@RequestParam Long id) {
        TopicDtoShort response = topicService.unarchiveTopic(id);
        return ResponseEntity.ok(response);
    }


}
