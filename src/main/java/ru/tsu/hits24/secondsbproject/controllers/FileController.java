package ru.tsu.hits24.secondsbproject.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.file.FileDto;
import ru.tsu.hits24.secondsbproject.dto.message.MessageDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.repository.FileRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.MessageRepository;
import ru.tsu.hits24.secondsbproject.service.MessageFileService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final MessageFileService messageFileService;

    @GetMapping("/{messageId}/getAllFiles")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<List<FileDto>> getMessageFiles(@PathVariable(value = "messageId") Long messageId){
        return ResponseEntity.ok(messageFileService.GetAllFilesForMessage(messageId));
    }

    @PostMapping("/upload/{messageId}")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<FileDto> upload(@PathVariable(value = "messageId") Long messageId, @RequestParam("file")MultipartFile file){
        return ResponseEntity.ok(messageFileService.upload(file, messageId));
    }

    @GetMapping("/download/{id}")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<byte[]> download(@PathVariable(value = "id") String id){
        var file = messageFileService.downLoad(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", file.getFileName())
                .body(file.getContent());
    }

    @DeleteMapping("/delete/{id}")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable(value = "id") String id){
        messageFileService.delete(id);
        return ResponseEntity.ok(null);
    }
    

}
