package com.example.chirper.service;

import com.example.chirper.config.AppConfiguration;
import com.example.chirper.persistence.entity.FileAttachment;
import com.example.chirper.persistence.entity.repository.FileAttachmentRepository;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class FileServiceTest {


    private FileService fileService;

    private AppConfiguration appConfiguration;

    @MockBean
    private FileAttachmentRepository fileAttachmentRepository;

    @BeforeEach
    public void init() {
        appConfiguration = new AppConfiguration();
        appConfiguration.setUploadPath("uploads-test");

        fileService = new FileService(appConfiguration, new Tika(), fileAttachmentRepository);

        new File(appConfiguration.getUploadPath()).mkdir();
        new File(appConfiguration.getFullProfileImagesPath()).mkdir();
        new File(appConfiguration.getFullAttachmentsPath()).mkdir();
    }
    @Test
    public void detectType_whenPngFileProvided_returnsImagePng() throws IOException {
        ClassPathResource resourceFile = new ClassPathResource("test-png.png");
        byte[] fileArr = FileUtils.readFileToByteArray(resourceFile.getFile());
        String fileType = fileService.detectType(fileArr);
        assertThat(fileType).isEqualToIgnoringCase("image/png");
    }
    @Test
    public void cleanupStorage_whenOldFileExist_removesFilesFromStorage() throws IOException {
        String fileName = "random-file";
        String filePath = appConfiguration.getFullAttachmentsPath() + "/" + fileName;
        File source = new ClassPathResource("profile.png").getFile();
        File target = new File(filePath);
        FileUtils.copyFile(source, target);

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setId(5L);
        fileAttachment.setName(fileName);

        when(fileAttachmentRepository.findByDateBeforeAndPostIsNull(any(LocalDateTime.class)))
                .thenReturn(List.of(fileAttachment));

        fileService.cleanupStorage();

        File storedImage = new File(filePath);
        assertThat(storedImage.exists()).isFalse();
    }
    @Test
    public void cleanupStorage_whenOldFileExist_removesFileAttachmentFromDatabase() throws IOException {
        String fileName = "random-file";
        String filePath = appConfiguration.getFullAttachmentsPath() + "/" + fileName;
        File source = new ClassPathResource("profile.png").getFile();
        File target = new File(filePath);
        FileUtils.copyFile(source, target);

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setId(5L);
        fileAttachment.setName(fileName);

        when(fileAttachmentRepository.findByDateBeforeAndPostIsNull(any(LocalDateTime.class)))
                .thenReturn(List.of(fileAttachment));

        fileService.cleanupStorage();

        verify(fileAttachmentRepository).deleteById(5L);
    }


    @AfterEach
    public void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfiguration.getFullAttachmentsPath()));
    }
}
