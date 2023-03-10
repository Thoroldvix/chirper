package com.example.hoaxify.dto;

import com.example.hoaxify.validation.ProfileImage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {

    @NotNull
    @Size(min = 4, max = 255)
    private String displayName;

    @ProfileImage
    private String image;
}
