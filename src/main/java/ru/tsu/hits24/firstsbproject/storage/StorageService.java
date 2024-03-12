package ru.tsu.hits24.firstsbproject.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu.hits24.firstsbproject.dto.FileInfoDto;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    String store(MultipartFile file);

    Stream<Path> loadAll();

    List<FileInfoDto> listAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    String getOriginalFilenameById(String fileId);


}
