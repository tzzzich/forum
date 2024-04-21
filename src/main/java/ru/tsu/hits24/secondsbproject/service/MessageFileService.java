package ru.tsu.hits24.secondsbproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu.hits24.secondsbproject.Utils.CategoryUtils;
import ru.tsu.hits24.secondsbproject.dto.file.DownloadFileDto;
import ru.tsu.hits24.secondsbproject.dto.file.FileDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.fileStorage.impl.MinioFileService;
import ru.tsu.hits24.secondsbproject.jpa.entity.CategoryEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.FileEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.MessageEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
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
        UserEntity user = userService.getCurrentUser();
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new InvalidArgumentsException("Invalid message id"));
        if (message.getCreator() != user) {
            throw new PermissionDeniedException("User is not permitted to upload files to this message");
        }
            FileEntity newFile = new FileEntity();
            newFile.setMessage(message);
            newFile.setFileName(file.getOriginalFilename());
            newFile.setFileSize(file.getSize());
            newFile.setCreationTime(LocalDateTime.now());
            newFile.setCreator(userService.getCurrentUser());
            try {
                newFile.setId(minioFileService.upload(file.getBytes()));
            }catch (Exception e) {
                throw new InvalidArgumentsException(e.getMessage());
            }
            try {
                fileRepository.save(newFile);
            }
            catch (Exception e) {
                throw new DatabaseException(e.getMessage());
            }
            return new FileDto(newFile.getId(), newFile.getFileName(), newFile.getFileSize());
    }

    public DownloadFileDto downLoad(String id) {
        UserEntity user = userService.getCurrentUser();
        try {
            DownloadFileDto result = new DownloadFileDto();
            result.setFileName(fileRepository.findById(id).orElseThrow().getFileName());
            result.setContent(minioFileService.download(id));
            return result;
        } catch (NoSuchElementException ex) {
            throw new InvalidArgumentsException("Invalid message id");
        } catch (Exception e) {
            throw new RuntimeException("File upload error: " + e.getMessage());
        }
    }

    public boolean delete(String id){
        UserEntity user = userService.getCurrentUser();
        FileEntity file= fileRepository.findById(id)
                .orElseThrow(() -> new DatabaseException("Cannot find file with id: " + id));
        CategoryEntity category = file.getMessage().getTopic().getCategory();
        if (!userService.isAdmin(user)
                && !CategoryUtils.canEditCategory(user, category)
                && file.getMessage().getCreator() != user) {
            throw new PermissionDeniedException("User is not permitted to delete files of this message");
        }
        try{
            minioFileService.delete(id);
            return true;
        }catch (Exception e){
            throw new RuntimeException("Error deleting file with id: " + id);
        }
    }

}
