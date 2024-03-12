package ru.tsu.hits24.firstsbproject.storage;

import org.springframework.stereotype.Service;
import ru.tsu.hits24.firstsbproject.dto.FileInfoDto;

import java.util.*;

@Service
public class FileInfoStorage {
    private final Map<String, FileInfoDto> fileMapping = new HashMap<>();

    public void addFile(String fileId, String name, long fileSize) {
        fileMapping.put(fileId, new FileInfoDto(fileId, name, fileSize));
    }

    public String findFileName(String fileId) {
        String filename = fileMapping.get(fileId).getFileName();

        if (filename != null) {
            return filename;
        } else {
            throw new NoSuchElementException("FileId not found: " + fileId);
        }
    }

    public List<FileInfoDto> getFilesInfo() {
        List<FileInfoDto> filesList = new ArrayList<>(fileMapping.values());
        if (filesList != null) {
            return filesList;
        } else {
            throw new NoSuchElementException("Files not found");
        }
    }

}
