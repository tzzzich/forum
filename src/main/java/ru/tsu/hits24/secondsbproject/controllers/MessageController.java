package ru.tsu.hits24.secondsbproject.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageCreateDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageEditDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.service.MessageService;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("create")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Long> createMessage(@Valid @RequestBody MessageCreateDto data){
        Long id = messageService.createMessage(data);
        return ResponseEntity.ok(id);
    }

    @GetMapping("messageId")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<MessageDto> getMessage(@RequestParam Long id) {
        MessageDto response = messageService.getMessage( id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("getAllMessages")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Page<MessageDto>> getAllMessagesByPage(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(required = false) Long topicId) {
        Page<MessageDto> response = messageService.getMessagesByPage(page, size, topicId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("messagesByContent")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Page<MessageDto>> getMessagesByContent(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(required = false) String content) {
        Page<MessageDto> response = messageService.getMessagesByPageByContent(page, size, content);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Page<MessageDto>> searchMessages(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MessageDto> messages = messageService.searchMessages(text, startDate, endDate, author, topicId, categoryId, page, size);
        return ResponseEntity.ok(messages);
    }



    @PatchMapping("edit")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<MessageDto> editMessage(@Valid @RequestBody MessageEditDto data, @RequestParam Long id) {
        MessageDto response = messageService.editMessage(data, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> deleteMessage(@RequestParam Long id) {
        ResponseDto response = messageService.deleteMessage(id);
        return ResponseEntity.ok(response);
    }


}
