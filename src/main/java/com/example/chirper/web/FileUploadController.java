package com.example.chirper.web;

import com.example.chirper.persistence.entity.FileAttachment;
import com.example.chirper.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/1.0")
public class FileUploadController {

    private final FileService fileService;

    @Autowired
    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/posts/upload")
    public FileAttachment uploadForHoax(MultipartFile file) {

        return fileService.saveAttachment(file);
    }
}
