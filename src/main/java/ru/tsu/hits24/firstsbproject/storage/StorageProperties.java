package ru.tsu.hits24.firstsbproject.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("storage")
public class StorageProperties {

    private String location = "D:\\java-upload-files";

}
