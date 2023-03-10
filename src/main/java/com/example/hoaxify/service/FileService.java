package com.example.hoaxify.service;

import com.example.hoaxify.config.AppConfiguration;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {

    private final AppConfiguration appConfiguration;

    private final Tika tika;

    @Autowired
    public FileService(AppConfiguration appConfiguration, Tika tika) {
        this.appConfiguration = appConfiguration;
        this.tika = tika;
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

    public String detectType(byte[] fileArr) {
        return tika.detect(fileArr);
    }


    public void deleteProfileImage(String image) {
        try {
            Files.deleteIfExists(Path.of(appConfiguration.getFullProfileImagesPath(), image));
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
}
