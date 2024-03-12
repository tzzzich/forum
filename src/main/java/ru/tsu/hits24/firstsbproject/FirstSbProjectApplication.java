package ru.tsu.hits24.firstsbproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.tsu.hits24.firstsbproject.storage.StorageProperties;
import ru.tsu.hits24.firstsbproject.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class FirstSbProjectApplication {

	public static void main(String[] args) {

		SpringApplication.run(FirstSbProjectApplication.class, args);

	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

}
