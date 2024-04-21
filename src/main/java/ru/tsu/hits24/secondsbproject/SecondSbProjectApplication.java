package ru.tsu.hits24.secondsbproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("ru.tsu.hits24.secondsbproject")
@SpringBootApplication
public class SecondSbProjectApplication {

	public static void main(String[] args) {

		SpringApplication.run(SecondSbProjectApplication.class, args);

	}

}
