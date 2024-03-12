package ru.tsu.hits24.firstsbproject.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Data
public class FileInfoDto {
    String id;
    String fileName;
    long fileSize;
    LocalDateTime uploadTime;

    public FileInfoDto(String id, String fileName, long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.uploadTime = LocalDateTime.now();
    }

}
