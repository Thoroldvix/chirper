package com.example.hoaxify.validation;

import com.example.hoaxify.service.FileService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;

public class ProfileImageValidator implements ConstraintValidator<ProfileImage, String> {

    private final FileService fileService;

    @Autowired
    public ProfileImageValidator(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(value);
        String fileType = fileService.detectType(decodedBytes);
        return fileType.equalsIgnoreCase("image/png") || fileType.equalsIgnoreCase("image/jpeg");
    }
}
