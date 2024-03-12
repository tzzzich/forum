package ru.tsu.hits24.firstsbproject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


import ru.tsu.hits24.firstsbproject.dto.FileInfoDto;
import ru.tsu.hits24.firstsbproject.storage.StorageService;
import ru.tsu.hits24.firstsbproject.storage.exceptions.StorageFileNotFoundException;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    private final StorageService storageService;
    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }


    @GetMapping("list")
    @ResponseBody
    public ResponseEntity<List<FileInfoDto>> listUploadedFiles() throws IOException {

        List<FileInfoDto> uploadedFilesInfo = storageService.listAll();

        return ResponseEntity.ok().body(uploadedFilesInfo);
    }

    @GetMapping("urls")
    @ResponseBody
    public ResponseEntity<List<String>> listUploadedFilesUrls() throws IOException {
        List<String> fileUrls = storageService.loadAll()
                .map(path -> MvcUriComponentsBuilder.fromMethodName(FileController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(fileUrls);
    }

    @GetMapping("/file")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@RequestParam String fileId) {

        Resource file = storageService.loadAsResource(fileId);

        if (file == null)
            return ResponseEntity.notFound().build();

        String originalFilename = storageService.getOriginalFilenameById(fileId);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(originalFilename, StandardCharsets.UTF_8)
                .build());

        return ResponseEntity.ok().headers(headers).body(file);
    }


    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            String fileId = storageService.store(file);
            return ResponseEntity.ok(fileId);
        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.badRequest().body(e.getBody());
        }
    }



    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
