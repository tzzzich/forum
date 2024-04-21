package ru.tsu.hits24.secondsbproject.fileStorage.impl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tsu.hits24.secondsbproject.fileStorage.FileService;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService implements FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public String upload(byte[] content) {
        try {
            String id = UUID.randomUUID().toString();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(id)
                    .stream(new ByteArrayInputStream(content), content.length, -1)
                    .build());
            return id;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] download(String id) {
        var args = GetObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(id)
                .build();
        try(var in = minioClient.getObject(args)){
            return in.readAllBytes();
        }
        catch (Exception e){
            throw new RuntimeException("Failed to download file with id: " + id);
        }
    }

    public boolean delete(String id){
        try{
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(id)
                    .build());
            return true;
        } catch (Exception ex){
            throw new RuntimeException("Error deleting file with id: "+id);
        }
    }
}
