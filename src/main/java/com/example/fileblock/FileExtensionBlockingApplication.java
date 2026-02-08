package com.example.fileblock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FileExtensionBlockingApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileExtensionBlockingApplication.class, args);
    }
}
