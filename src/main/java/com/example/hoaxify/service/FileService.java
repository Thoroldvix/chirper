package com.example.hoaxify.service;

import com.example.hoaxify.config.AppConfiguration;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {

    private final AppConfiguration appConfiguration;

    @Autowired
    public FileService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public String saveProfileImage(String base64Image) throws IOException {
        String imageName = generateRandomImageName();

        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

        File target = new File(appConfiguration.getFullProfileImagesPath() + "/" + imageName);

        FileUtils.writeByteArrayToFile(target, decodedBytes);
        return imageName;
    }

    private String generateRandomImageName() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
