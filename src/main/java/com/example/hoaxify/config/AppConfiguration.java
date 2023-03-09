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

   private String profileImagesFolderPath = "profile";

    private String attachmentsFolderPath = "attachments";

    public  String getFullProfileImagePath() {
        return Path.of(this.uploadPath, profileImagesFolderPath).toString();
    }

    public String getFullAttachmentsPath() {
        return Path.of(this.uploadPath, attachmentsFolderPath).toString();
    }
}
