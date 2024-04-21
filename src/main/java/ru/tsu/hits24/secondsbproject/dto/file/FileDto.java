package ru.tsu.hits24.secondsbproject.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    private String id;

    private String fileName;

    private Long fileSize;
}
