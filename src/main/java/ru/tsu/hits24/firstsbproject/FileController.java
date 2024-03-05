package ru.tsu.hits24.firstsbproject;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @SneakyThrows
    @PostMapping("/upload")
    public void upload(@RequestParam("myFile")MultipartFile file) {
        log.info("Название файла: {}", file.getOriginalFilename());
        log.info("Размер файла: {}", file.getSize());
        log.info("Контент: {}", new String(file.getBytes()));
    }
}
