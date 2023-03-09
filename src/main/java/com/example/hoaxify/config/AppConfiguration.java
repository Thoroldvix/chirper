package com.example.hoaxify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Data
@Configuration
@ConfigurationProperties(prefix = "hoaxify")
public class AppConfiguration {
   private String uploadPath;

   private String profileImagesFolder = "profile";

    private String attachmentsFolder = "attachments";

    public  String getFullProfileImagesPath() {
        return Path.of(this.uploadPath, profileImagesFolder).toString();
    }

    public String getFullAttachmentsPath() {
        return Path.of(this.uploadPath, attachmentsFolder).toString();
    }
}
