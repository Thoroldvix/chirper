package com.example.chirper.service;

import com.example.chirper.config.AppConfiguration;
import com.example.chirper.persistence.entity.FileAttachment;
import com.example.chirper.persistence.entity.repository.FileAttachmentRepository;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@EnableScheduling
public class FileService {

    private final int SIXTY_MINUTES_IN_MILLIS = 1000 * 60 * 60;
    private final AppConfiguration appConfiguration;

    private final Tika tika;

    private final FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    public FileService(AppConfiguration appConfiguration, Tika tika, FileAttachmentRepository fileAttachmentRepository) {
        this.appConfiguration = appConfiguration;
        this.tika = tika;
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

    public String saveProfileImage(String base64Image) throws IOException {
        String imageName = generateRandomFileName();

        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

        File target = new File(appConfiguration.getFullProfileImagesPath() + "/" + imageName);

        FileUtils.writeByteArrayToFile(target, decodedBytes);
        return imageName;
    }

    public void deleteProfileImage(String image) {
        try {
            Files.deleteIfExists(Path.of(appConfiguration.getFullProfileImagesPath(), image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public FileAttachment saveAttachment(MultipartFile file) {
        String randomName = generateRandomFileName();
        File target = new File(appConfiguration.getFullAttachmentsPath() + "/" + randomName);
        FileAttachment fileAttachment = FileAttachment.builder()
                .date(LocalDateTime.now())
                .name(randomName)
                .build();
        try {
            byte[] fileAsBytes = file.getBytes();
            FileUtils.writeByteArrayToFile(target, fileAsBytes);
            fileAttachment.setFileType(detectType(fileAsBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileAttachmentRepository.save(fileAttachment);
    }

    private String generateRandomFileName() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String detectType(byte[] fileArr) {
        return tika.detect(fileArr);
    }

    @Scheduled(fixedRate = SIXTY_MINUTES_IN_MILLIS)
    public void cleanupStorage() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        fileAttachmentRepository.findByDateBeforeAndPostIsNull(oneHourAgo)
                .forEach(attachment -> {
                    deleteAttachmentImage(attachment.getName());
                    fileAttachmentRepository.deleteById(attachment.getId());
                });
    }


    private void deleteAttachmentImage(String image) {
        try {
            Files.deleteIfExists(Path.of(appConfiguration.getFullAttachmentsPath(), image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
