package ru.tsu.hits24.secondsbproject.fileStorage;

public interface FileService {
    String upload(byte[] content);
    byte[] download(String id);
}
