package ru.tsu.hits24.secondsbproject.dto.file;

import lombok.Data;

@Data
public class DownloadFileDto {
    private byte[] content;
    private String fileName;
}
