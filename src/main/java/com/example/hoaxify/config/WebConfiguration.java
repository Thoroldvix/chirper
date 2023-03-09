package com.example.hoaxify.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {


    private final AppConfiguration appConfiguration;

    @Autowired
    public WebConfiguration(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }


    @Bean
    public CommandLineRunner createUploadFolder() {
        return (args) -> {
            createNonExistingFolder(appConfiguration.getUploadPath());
            createNonExistingFolder(appConfiguration.getFullProfileImagePath());
            createNonExistingFolder(appConfiguration.getFullAttachmentsPath());
        };
    }

    private void createNonExistingFolder(String path) {
        File folder = new File(path);
        boolean folderExist = folder.exists() && folder.isDirectory();
        if (!folderExist) {
            folder.mkdir();
        }
    }
}
