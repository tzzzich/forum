package ru.tsu.hits24.secondsbproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu.hits24.secondsbproject.dto.file.DownloadFileDto;
import ru.tsu.hits24.secondsbproject.dto.file.FileDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.fileStorage.impl.MinioFileService;
import ru.tsu.hits24.secondsbproject.jpa.entity.FileEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.FileRepository;
import ru.tsu.hits24.secondsbproject.jpa.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageFileService {

    private final FileRepository fileRepository;

    private final MessageRepository messageRepository;

    private final MinioFileService minioFileService;

    private final UserService userService;

    public List<FileDto> GetAllFilesForMessage(Long messageId){
        List<FileDto> result = new ArrayList<FileDto>();
        messageRepository.findById(messageId).orElseThrow(() -> new InvalidArgumentsException("Invalid message id"));
        fileRepository.findAllByMessageId(messageId).forEach(f->result.add(new FileDto(f.getId(), f.getFileName(), f.getFileSize())));
        return result;
    }

    @Transactional
    public FileDto upload(MultipartFile file, Long messageId) {
        try {
            FileEntity newFile = new FileEntity();
            newFile.setMessage(messageRepository.findById(messageId).orElseThrow());
            newFile.setFileName(file.getOriginalFilename());
            newFile.setFileSize(file.getSize());
            newFile.setCreationTime(LocalDateTime.now());
            newFile.setCreator(userService.getCurrentUser());
            newFile.setId(minioFileService.upload(file.getBytes()));
            fileRepository.save(newFile);
            return new FileDto(newFile.getId(), newFile.getFileName(), newFile.getFileSize());
        }catch (NoSuchElementException ex){
            throw new InvalidArgumentsException("Invalid message id");
        }catch (Exception e){
            throw new RuntimeException("File upload error");
        }
    }

    public DownloadFileDto downLoad(String id) {
        try {
            DownloadFileDto result = new DownloadFileDto();
            result.setFileName(fileRepository.findById(id).orElseThrow().getFileName());
            result.setContent(minioFileService.download(id));
            return result;
        } catch (NoSuchElementException ex) {
            throw new InvalidArgumentsException("Invalid message id");
        } catch (Exception e) {
            throw new RuntimeException("File upload error");
        }
    }

    public boolean delete(String id){
        try{
            minioFileService.delete(id);
            return true;
        }catch (Exception e){
            throw new RuntimeException("Error deleting file with id: " + id);
        }
    }

}
