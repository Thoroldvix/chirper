package com.example.hoaxify.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    public CommandLineRunner createUploadFolder() {
        return (args) -> {
            File uploadFolder= new File("uploads-test");
            boolean uploadFolderExist = uploadFolder.exists() && uploadFolder.isDirectory();
            if (!uploadFolderExist) {
                uploadFolder.mkdir();
            }
        };
    }
}
