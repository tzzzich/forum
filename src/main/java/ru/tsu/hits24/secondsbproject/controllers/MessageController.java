package ru.tsu.hits24.secondsbproject.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageCreateDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageEditDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicCreateDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDto;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicDtoShort;
import ru.tsu.hits24.secondsbproject.dto.topic.TopicEditDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.service.MessageService;
import ru.tsu.hits24.secondsbproject.jpa.service.TopicService;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService){
        this.messageService = messageService;
    }

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

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ResponseDto> handleDatabaseException (DatabaseException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(InvalidArgumentsException.class)
    public ResponseEntity<ResponseDto> handleInvalidArgumentsException (InvalidArgumentsException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ResponseDto> handlePermissionDeniedException (PermissionDeniedException ex) {
        ResponseDto errorResponse = new ResponseDto("Forbidden", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception ex) {
        ResponseDto errorResponse = new ResponseDto("Internal Server Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
